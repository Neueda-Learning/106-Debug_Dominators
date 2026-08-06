package com.paymentprocessing.payment_processing_system.util;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static String generateReferenceNumber() {
        return "REF-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    public static String generateIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
}