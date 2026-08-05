package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryResponse;
import com.paymentprocessing.payment_processing_system.service.PaymentHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/payment-history")
public class PaymentHistoryController {


    private final PaymentHistoryService service;


    public PaymentHistoryController(
            PaymentHistoryService service) {

        this.service = service;
    }



    @PostMapping
    public ResponseEntity<PaymentHistoryResponse> createHistory(
            @RequestBody PaymentHistoryRequest request) {


        return new ResponseEntity<>(
                service.createHistory(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    public ResponseEntity<List<PaymentHistoryResponse>> getAllHistory() {

        return ResponseEntity.ok(
                service.getAllHistory()
        );
    }



    @GetMapping("/{id}")
    public ResponseEntity<PaymentHistoryResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getHistoryById(id)
        );
    }



    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<PaymentHistoryResponse>>
    getByPaymentId(
            @PathVariable Long paymentId) {


        return ResponseEntity.ok(
                service.getHistoryByPaymentId(paymentId)
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {


        service.deleteHistory(id);

        return ResponseEntity.ok(
                "Payment history deleted successfully"
        );
    }
}