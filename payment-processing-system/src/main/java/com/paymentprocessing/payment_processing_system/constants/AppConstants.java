package com.paymentprocessing.payment_processing_system.constants;

public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // ===========================
    // SUCCESS MESSAGES
    // ===========================

    public static final String PAYMENT_CREATED =
            "Payment created successfully.";

    public static final String PAYMENT_UPDATED =
            "Payment updated successfully.";

    public static final String PAYMENT_DELETED =
            "Payment deleted successfully.";

    public static final String PAYMENT_FETCHED =
            "Payment fetched successfully.";

    public static final String PAYMENT_HISTORY_FETCHED =
            "Payment history fetched successfully.";

    public static final String REFUND_CREATED =
            "Refund created successfully.";

    public static final String REFUND_UPDATED =
            "Refund updated successfully.";

    public static final String CAMPAIGN_CREATED =
            "Campaign created successfully.";

    public static final String CONTRIBUTION_CREATED =
            "Contribution created successfully.";

    public static final String NOTIFICATION_SENT =
            "Notification sent successfully.";

    public static final String EXCHANGE_RATE_UPDATED =
            "Exchange rate updated successfully.";

    // ===========================
    // ERROR MESSAGES
    // ===========================

    public static final String PAYMENT_NOT_FOUND =
            "Payment not found.";

    public static final String PAYMENT_ALREADY_EXISTS =
            "Payment already exists.";

    public static final String INVALID_AMOUNT =
            "Invalid payment amount.";

    public static final String INVALID_ACCOUNT =
            "Invalid account details.";

    public static final String INVALID_CURRENCY =
            "Invalid currency.";

    public static final String DUPLICATE_PAYMENT =
            "Duplicate payment request.";

    public static final String INVALID_STATUS =
            "Invalid payment status.";

    public static final String INVALID_STATUS_TRANSITION =
            "Invalid status transition.";

    public static final String INSUFFICIENT_FUNDS =
            "Insufficient funds.";

    public static final String NETWORK_ERROR =
            "Network error occurred.";

    public static final String PROCESSING_ERROR =
            "Payment processing failed.";

    public static final String REFUND_NOT_FOUND =
            "Refund not found.";

    public static final String CAMPAIGN_NOT_FOUND =
            "Campaign not found.";

    public static final String CONTRIBUTION_NOT_FOUND =
            "Contribution not found.";

    public static final String CRYPTO_TRANSACTION_NOT_FOUND =
            "Crypto transaction not found.";

    public static final String EXCHANGE_RATE_NOT_FOUND =
            "Exchange rate not found.";

    // ===========================
    // VALIDATION
    // ===========================

    public static final double MIN_PAYMENT_AMOUNT = 1.00;

    public static final double MAX_PAYMENT_AMOUNT = 1000000.00;

    public static final int MAX_DESCRIPTION_LENGTH = 255;

    public static final int MAX_REFERENCE_LENGTH = 100;

    // ===========================
    // DEFAULT VALUES
    // ===========================

    public static final int DEFAULT_RETRY_COUNT = 0;

    public static final String DEFAULT_COUNTRY = "India";

    public static final String DEFAULT_CURRENCY = "INR";

}