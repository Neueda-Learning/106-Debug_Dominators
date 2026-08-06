package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.RetryResponse;
import com.paymentprocessing.payment_processing_system.service.RetryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/retry")
@Tag(
        name = "Payment Retry APIs",
        description = "APIs for retrying failed payments"
)
public class RetryController {


    private final RetryService retryService;


    public RetryController(
            RetryService retryService) {

        this.retryService = retryService;
    }



    @PostMapping("/{paymentId}")
    @Operation(
            summary = "Retry failed payment",
            description = "Retries a failed payment and updates retry count"
    )
    public ResponseEntity<RetryResponse> retryPayment(
            @PathVariable Long paymentId) {


        return ResponseEntity.ok(
                retryService.retryPayment(paymentId)
        );
    }
}
