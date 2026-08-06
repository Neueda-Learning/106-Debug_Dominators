package com.paymentprocessing.payment_processing_system.controller;

import com.paymentprocessing.payment_processing_system.dto.CryptoRequest;
import com.paymentprocessing.payment_processing_system.dto.CryptoResponse;
import com.paymentprocessing.payment_processing_system.service.CryptoPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/crypto-payments")
@Tag(name = "Crypto Payment APIs", description = "Operations for managing cryptocurrency-based payment transactions")
public class CryptoPaymentController {


    private final CryptoPaymentService cryptoPaymentService;


    public CryptoPaymentController(
            CryptoPaymentService cryptoPaymentService) {

        this.cryptoPaymentService = cryptoPaymentService;
    }


    @PostMapping
    @Operation(
            summary = "Create Crypto Payment",
            description = "Creates a new crypto payment record linked to a payment transaction"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Crypto payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Referenced payment resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @Operation(
            summary = "Get All Crypto Payments",
            description = "Retrieves all crypto payment transactions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Crypto payments retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Crypto payment resources not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CryptoResponse>> getAllCryptoPayments() {

        return ResponseEntity.ok(
                cryptoPaymentService.getAllCryptoPayments()
        );
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get Crypto Payment By Id",
            description = "Retrieves a crypto payment transaction by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Crypto payment retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Crypto payment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CryptoResponse> getCryptoPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cryptoPaymentService.getCryptoPaymentById(id)
        );
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Crypto Payment",
            description = "Deletes a crypto payment transaction by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Crypto payment deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Crypto payment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteCryptoPayment(
            @PathVariable Long id) {

        cryptoPaymentService.deleteCryptoPayment(id);

        return ResponseEntity.ok(
                "Crypto payment deleted successfully"
        );
    }
}