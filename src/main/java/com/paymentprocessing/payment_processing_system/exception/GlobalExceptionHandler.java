package com.paymentprocessing.payment_processing_system.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(
            PaymentNotFoundException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex,
                HttpStatus.NOT_FOUND,
                "PAYMENT_NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(RefundNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRefundNotFoundException(
            RefundNotFoundException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex,
                HttpStatus.NOT_FOUND,
                "REFUND_NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePaymentException(
            DuplicatePaymentException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex,
                HttpStatus.CONFLICT,
                "DUPLICATE_PAYMENT",
                request
        );
    }

    @ExceptionHandler({
            InvalidAmountException.class,
            InvalidAccountException.class,
            InvalidCurrencyException.class,
            ValidationFailedException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(
            Exception ex,
            HttpServletRequest request) {

        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            String message = methodArgumentNotValidException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .findFirst()
                    .map(error -> error.getDefaultMessage())
                    .orElse("Validation failed");

            return buildErrorResponse(
                    ex,
                    HttpStatus.BAD_REQUEST,
                    "VALIDATION_ERROR",
                    message,
                    request
            );
        }

        return buildErrorResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                request
        );
    }

    @ExceptionHandler({
            InvalidPaymentException.class,
            RefundException.class,
            CampaignException.class,
            ContributionException.class,
            CryptoPaymentException.class,
            ExchangeRateException.class,
            InsufficientFundsException.class,
            InvalidStatusTransitionException.class,
            NetworkException.class,
            NotificationException.class,
            ProcessingException.class,
            AuditException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessException(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "BUSINESS_ERROR",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            String errorCode,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex,
                status,
                errorCode,
                ex.getMessage(),
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            String errorCode,
            String message,
            HttpServletRequest request) {

        log.error(
                "Exception handled | type={} | status={} | path={} | message={}",
                ex.getClass().getSimpleName(),
                status.value(),
                request.getRequestURI(),
                message,
                ex
        );

        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setError(errorCode);
        response.setMessage(message);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, status);
    }

}
