package com.paymentprocessing.payment_processing_system.service.impl;

import com.paymentprocessing.payment_processing_system.dto.StatementResponse;
import com.paymentprocessing.payment_processing_system.exception.PaymentNotFoundException;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.StatementService;
import com.paymentprocessing.payment_processing_system.util.PdfGenerator;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


@Service
public class StatementServiceImpl implements StatementService {


    private final PaymentRepository paymentRepository;

    private final PdfGenerator pdfGenerator;


    public StatementServiceImpl(
            PaymentRepository paymentRepository,
            PdfGenerator pdfGenerator) {

        this.paymentRepository = paymentRepository;
        this.pdfGenerator = pdfGenerator;
    }



    @Override
    public Resource generatePaymentStatement(
            Long paymentId) {


        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found with id: "
                                                + paymentId
                                )
                        );


        StatementResponse statement =
                new StatementResponse();


        statement.setPaymentId(
                payment.getPaymentId()
        );


        statement.setReferenceNumber(
                payment.getReferenceNumber()
        );


        statement.setAmount(
                payment.getAmount()
        );


        statement.setCurrency(
                payment.getCurrency().toString()
        );


        statement.setStatus(
                payment.getStatus().toString()
        );


        statement.setTransactionDate(
                payment.getCreatedAt()
        );


        statement.setDescription(
                payment.getDescription()
        );



        byte[] pdfBytes =
                pdfGenerator.generateStatement(statement);



        return new ByteArrayResource(pdfBytes);
    }
}