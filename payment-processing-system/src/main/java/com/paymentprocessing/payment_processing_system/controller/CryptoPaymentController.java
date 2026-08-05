package com.paymentprocessing.payment_processing_system.controller;

import com.paymentprocessing.payment_processing_system.dto.CryptoRequest;
import com.paymentprocessing.payment_processing_system.dto.CryptoResponse;
import com.paymentprocessing.payment_processing_system.service.CryptoPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/crypto-payments")
public class CryptoPaymentController {


    private final CryptoPaymentService cryptoPaymentService;


    public CryptoPaymentController(
            CryptoPaymentService cryptoPaymentService) {

        this.cryptoPaymentService = cryptoPaymentService;
    }


    @PostMapping
    public ResponseEntity<CryptoResponse> createCryptoPayment(
            @RequestBody CryptoRequest request) {

        CryptoResponse response =
                cryptoPaymentService.createCryptoPayment(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    @GetMapping
    public ResponseEntity<List<CryptoResponse>> getAllCryptoPayments() {

        return ResponseEntity.ok(
                cryptoPaymentService.getAllCryptoPayments()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<CryptoResponse> getCryptoPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cryptoPaymentService.getCryptoPaymentById(id)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCryptoPayment(
            @PathVariable Long id) {

        cryptoPaymentService.deleteCryptoPayment(id);

        return ResponseEntity.ok(
                "Crypto payment deleted successfully"
        );
    }
}