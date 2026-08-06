package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.PaymentRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentResponse;
import com.paymentprocessing.payment_processing_system.enums.ErrorCode;
import com.paymentprocessing.payment_processing_system.enums.PaymentMethod;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.model.PaymentHistory;
import com.paymentprocessing.payment_processing_system.repository.PaymentHistoryRepository;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.PaymentService;
import com.paymentprocessing.payment_processing_system.util.IdGenerator;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



@Service
public class PaymentServiceImpl implements PaymentService {


    private static final Logger log =
            LoggerFactory.getLogger(PaymentServiceImpl.class);



    private final PaymentRepository paymentRepository;

        private final PaymentHistoryRepository paymentHistoryRepository;

        private final ScheduledExecutorService scheduler =
                        Executors.newSingleThreadScheduledExecutor();

            // Demo wallet state per source account for live status simulation.
            private final Map<String, BigDecimal> usdBalanceByAccount =
                    new ConcurrentHashMap<>();

            private final Map<String, BigDecimal> btcBalanceByAccount =
                    new ConcurrentHashMap<>();

            private static final BigDecimal INITIAL_USD_BALANCE = new BigDecimal("10000");
            private static final BigDecimal INITIAL_BTC_BALANCE = new BigDecimal("2");
            private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
            private static final Map<String, BigDecimal> USD_RATES = buildUsdRates();



    public PaymentServiceImpl(
                        PaymentRepository paymentRepository,
                        PaymentHistoryRepository paymentHistoryRepository) {

        this.paymentRepository = paymentRepository;
                this.paymentHistoryRepository = paymentHistoryRepository;
    }





    @Override
    public PaymentResponse createPayment(
            PaymentRequest paymentRequest) {


        log.info(
                "Creating payment from account: {} to account: {}",
                paymentRequest.getSourceAccount(),
                paymentRequest.getDestinationAccount()
        );


        Payment payment =
                mapRequestToEntity(paymentRequest);



        Payment savedPayment =
                paymentRepository.save(payment);


        recordHistory(
                savedPayment,
                null,
                PaymentStatus.CREATED,
                "PAYMENT_CREATED",
                "Payment initiated by customer",
                "CUSTOMER"
        );


        scheduleStatusTransitions(savedPayment);


        log.info(
                "Payment created successfully with id: {}",
                savedPayment.getId()
        );


        return mapToResponse(savedPayment);
    }



    private void scheduleStatusTransitions(
            Payment payment) {


        scheduleStatusTransition(
                payment.getId(),
                PaymentStatus.CREATED,
                PaymentStatus.PROCESSING,
                3,
                "PAYMENT_PROCESSING",
                "Payment sent to gateway",
                "PAYMENT_GATEWAY"
        );


        scheduler.schedule(
                () -> resolveProcessingOutcome(payment.getId()),
                6,
                TimeUnit.SECONDS
        );
    }



    private void scheduleStatusTransition(
            Long paymentId,
            PaymentStatus expectedCurrentStatus,
            PaymentStatus nextStatus,
            long delaySeconds,
            String eventType,
            String remarks,
            String changedBy) {


        scheduler.schedule(() ->
                        paymentRepository.findById(paymentId)
                                .ifPresent(payment -> {
                                    if (payment.getStatus() != expectedCurrentStatus) {
                                        return;
                                    }

                                    PaymentStatus previousStatus = payment.getStatus();
                                    payment.setStatus(nextStatus);
                                    payment.setUpdatedAt(LocalDateTime.now());
                                    Payment updatedPayment = paymentRepository.save(payment);

                                    recordHistory(
                                            updatedPayment,
                                            previousStatus,
                                            nextStatus,
                                            eventType,
                                            remarks,
                                            changedBy
                                    );
                                }),
                delaySeconds,
                TimeUnit.SECONDS
        );
    }



    private void recordHistory(
            Payment payment,
            PaymentStatus oldStatus,
            PaymentStatus newStatus,
            String eventType,
            String remarks,
            String changedBy) {


        PaymentHistory history = new PaymentHistory();
        history.setPaymentId(payment.getId());
        history.setOldStatus(oldStatus != null ? oldStatus : newStatus);
        history.setNewStatus(newStatus);
        history.setEventType(eventType);
        history.setRemarks(remarks);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        paymentHistoryRepository.save(history);
    }



        private void resolveProcessingOutcome(
                        Long paymentId) {


                paymentRepository.findById(paymentId)
                                .ifPresent(payment -> {
                                        if (payment.getStatus() != PaymentStatus.PROCESSING) {
                                                return;
                                        }


                                        PaymentStatus previousStatus = payment.getStatus();
                                        BigDecimal amount = payment.getAmount() == null
                                                        ? BigDecimal.ZERO
                                                        : payment.getAmount();

                                        if (!hasSufficientBalance(payment, amount)) {
                                                payment.setStatus(PaymentStatus.FAILED);
                                                payment.setErrorCode(ErrorCode.INSUFFICIENT_FUNDS);
                                                payment.setErrorMessage("Insufficient balance");
                                                payment.setUpdatedAt(LocalDateTime.now());
                                                Payment updated = paymentRepository.save(payment);

                                                recordHistory(
                                                                updated,
                                                                previousStatus,
                                                                PaymentStatus.FAILED,
                                                                "INSUFFICIENT_FUNDS",
                                                                "Insufficient balance",
                                                                "PAYMENT_GATEWAY"
                                                );
                                                return;
                                        }


                                        debitBalance(payment, amount);
                                        payment.setStatus(PaymentStatus.COMPLETED);
                                        payment.setErrorCode(null);
                                        payment.setErrorMessage(null);
                                        payment.setUpdatedAt(LocalDateTime.now());
                                        Payment updated = paymentRepository.save(payment);

                                        recordHistory(
                                                        updated,
                                                        previousStatus,
                                                        PaymentStatus.COMPLETED,
                                                        "PAYMENT_SUCCESS",
                                                        "Payment completed successfully",
                                                        "PAYMENT_GATEWAY"
                                        );
                                });
        }



        private BigDecimal availableBalanceFor(
                        Payment payment) {


                if (payment.getCurrency() == null) {
                        return null;
                }


                String accountKey = normalizeAccount(payment.getSourceAccount());
                String currency = payment.getCurrency().name();

                if ("USD".equals(currency)) {
                        return usdBalanceByAccount.computeIfAbsent(
                                        accountKey,
                                        key -> INITIAL_USD_BALANCE
                        );
                }

                if ("BTC".equals(currency)) {
                        return btcBalanceByAccount.computeIfAbsent(
                                        accountKey,
                                        key -> INITIAL_BTC_BALANCE
                        );
                }

                return null;
        }



        private boolean hasSufficientBalance(
                        Payment payment,
                        BigDecimal amount) {


                if (payment.getCurrency() == null) {
                        return true;
                }

                String accountKey = normalizeAccount(payment.getSourceAccount());
                String currency = payment.getCurrency().name();

                if ("BTC".equals(currency)) {
                        BigDecimal btcAvailable = btcBalanceByAccount.computeIfAbsent(
                                        accountKey,
                                        key -> INITIAL_BTC_BALANCE
                        );
                        return btcAvailable.compareTo(amount) >= 0;
                }

                BigDecimal usdEquivalent = toUsdEquivalent(amount, currency);
                BigDecimal usdAvailable = usdBalanceByAccount.computeIfAbsent(
                                accountKey,
                                key -> INITIAL_USD_BALANCE
                );
                return usdAvailable.compareTo(usdEquivalent) >= 0;
        }



        private void debitBalance(
                        Payment payment,
                        BigDecimal amount) {


                if (payment.getCurrency() == null) {
                        return;
                }

                String accountKey = normalizeAccount(payment.getSourceAccount());
                String currency = payment.getCurrency().name();

                if ("USD".equals(currency)) {
                        usdBalanceByAccount.compute(
                                        accountKey,
                                        (key, current) -> (current == null ? INITIAL_USD_BALANCE : current)
                                                        .subtract(amount)
                        );
                } else if ("INR".equals(currency)
                                || "EUR".equals(currency)
                                || "GBP".equals(currency)
                                || "AED".equals(currency)
                                || "SGD".equals(currency)
                                || "ETH".equals(currency)
                                || "USDT".equals(currency)) {
                        BigDecimal usdEquivalent = toUsdEquivalent(amount, currency);
                        usdBalanceByAccount.compute(
                                        accountKey,
                                        (key, current) -> (current == null ? INITIAL_USD_BALANCE : current)
                                                        .subtract(usdEquivalent)
                        );
                } else if ("BTC".equals(currency)) {
                        btcBalanceByAccount.compute(
                                        accountKey,
                                        (key, current) -> (current == null ? INITIAL_BTC_BALANCE : current)
                                                        .subtract(amount)
                        );
                }
        }



        private BigDecimal toUsdEquivalent(
                        BigDecimal amount,
                        String currency) {


                BigDecimal rate = USD_RATES.get(currency);
                if (rate == null) {
                        return amount;
                }

                return amount.multiply(rate, MATH_CONTEXT);
        }



        private static Map<String, BigDecimal> buildUsdRates() {


                Map<String, BigDecimal> rates = new HashMap<>();
                rates.put("USD", BigDecimal.ONE);
                rates.put("INR", new BigDecimal("0.012"));
                rates.put("EUR", new BigDecimal("1.08"));
                rates.put("GBP", new BigDecimal("1.27"));
                rates.put("AED", new BigDecimal("0.27"));
                rates.put("SGD", new BigDecimal("0.74"));
                rates.put("ETH", new BigDecimal("3400"));
                rates.put("USDT", BigDecimal.ONE);
                return rates;
        }



        private String normalizeAccount(
                        String sourceAccount) {


                if (sourceAccount == null || sourceAccount.isBlank()) {
                        return "default-user";
                }

                return sourceAccount.trim().toLowerCase();
        }



    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
    }





    @Override
    public PaymentResponse getPaymentById(Long id) {


        log.info(
                "Fetching payment with id: {}",
                id
        );


        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Payment not found with id: {}",
                                    id
                            );


                            return new PaymentNotFoundException(
                                    "Payment not found with id: " + id
                            );
                        });



        return mapToResponse(payment);
    }





    @Override
    public List<PaymentResponse> getAllPayments() {


        log.info(
                "Fetching all payments"
        );


        List<PaymentResponse> responses =
                new ArrayList<>();


        paymentRepository.findAll()
                .forEach(payment ->
                        responses.add(
                                mapToResponse(payment)
                        ));



        return responses;
    }





    @Override
    public PaymentResponse updatePayment(
            Long id,
            PaymentRequest paymentRequest) {


        log.info(
                "Updating payment with id: {}",
                id
        );


        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Payment not found for update with id: {}",
                                    id
                            );


                            return new PaymentNotFoundException(
                                    "Payment not found with id: " + id
                            );
                        });



        payment.setSourceAccount(
                paymentRequest.getSourceAccount()
        );


        payment.setDestinationAccount(
                paymentRequest.getDestinationAccount()
        );


        payment.setAmount(
                paymentRequest.getAmount()
        );


        payment.setCurrency(
                paymentRequest.getCurrency()
        );


        payment.setPaymentMethod(
                paymentRequest.getPaymentMethod()
        );


        payment.setSourceCountry(
                paymentRequest.getSourceCountry()
        );


        payment.setDestinationCountry(
                paymentRequest.getDestinationCountry()
        );


        payment.setDescription(
                paymentRequest.getDescription()
        );


        payment.setUpdatedAt(
                LocalDateTime.now()
        );



        Payment updatedPayment =
                paymentRepository.save(payment);



        log.info(
                "Payment updated successfully with id: {}",
                updatedPayment.getId()
        );


        return mapToResponse(updatedPayment);
    }





    @Override
    public void deletePayment(Long id) {


        log.info(
                "Deleting payment with id: {}",
                id
        );


        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Payment not found for deletion with id: {}",
                                    id
                            );


                            return new PaymentNotFoundException(
                                    "Payment not found with id: " + id
                            );
                        });



        paymentRepository.delete(payment);



        log.info(
                "Payment deleted successfully with id: {}",
                id
        );
    }





    private Payment mapRequestToEntity(
            PaymentRequest request) {


        Payment payment =
                new Payment();


        payment.setPaymentId(
                IdGenerator.generatePaymentId()
        );


        payment.setReferenceNumber(
                IdGenerator.generateReferenceNumber()
        );


        payment.setIdempotencyKey(
                IdGenerator.generateIdempotencyKey()
        );


        payment.setSourceAccount(
                request.getSourceAccount()
        );


        payment.setDestinationAccount(
                request.getDestinationAccount()
        );


        payment.setAmount(
                request.getAmount()
        );


        payment.setCurrency(
                request.getCurrency()
        );


        payment.setPaymentMethod(
                request.getPaymentMethod()
        );


        payment.setSourceCountry(
                request.getSourceCountry()
        );


        payment.setDestinationCountry(
                request.getDestinationCountry()
        );


        payment.setDescription(
                request.getDescription()
        );


        payment.setRetryCount(0);


        payment.setStatus(
                PaymentStatus.CREATED
        );


        payment.setCreatedAt(
                LocalDateTime.now()
        );


        payment.setUpdatedAt(
                LocalDateTime.now()
        );


        return payment;
    }





    private PaymentResponse mapToResponse(
            Payment payment) {


        PaymentResponse response =
                new PaymentResponse();



        response.setId(
                payment.getId()
        );


        response.setPaymentId(
                payment.getPaymentId()
        );


        response.setReferenceNumber(
                payment.getReferenceNumber()
        );


        response.setSourceAccount(
                payment.getSourceAccount()
        );


        response.setDestinationAccount(
                payment.getDestinationAccount()
        );


        response.setAmount(
                payment.getAmount()
        );


        response.setCurrency(
                payment.getCurrency()
        );


        response.setPaymentMethod(
                payment.getPaymentMethod()
        );


        response.setStatus(
                payment.getStatus()
        );


        response.setSourceCountry(
                payment.getSourceCountry()
        );


        response.setDestinationCountry(
                payment.getDestinationCountry()
        );


        response.setDescription(
                payment.getDescription()
        );


        response.setCreatedAt(
                payment.getCreatedAt()
        );


        return response;
    }
}