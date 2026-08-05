package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryRequest;
import com.paymentprocessing.payment_processing_system.dto.PaymentHistoryResponse;
import com.paymentprocessing.payment_processing_system.service.PaymentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/payment-history")
@Tag(name = "Payment History APIs", description = "Operations for managing payment history records")
public class PaymentHistoryController {


    private final PaymentHistoryService service;


    public PaymentHistoryController(
            PaymentHistoryService service) {

        this.service = service;
    }



    @PostMapping
    @Operation(
            summary = "Create Payment History",
            description = "Creates a new payment history record for a payment transaction"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment history created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Referenced payment resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentHistoryResponse> createHistory(
            @RequestBody PaymentHistoryRequest request) {


        return new ResponseEntity<>(
                service.createHistory(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    @Operation(
            summary = "Get All Payment History",
            description = "Retrieves all payment history records"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "History resources not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentHistoryResponse>> getAllHistory() {

        return ResponseEntity.ok(
                service.getAllHistory()
        );
    }



    @GetMapping("/{id}")
    @Operation(
            summary = "Get Payment History By Id",
            description = "Retrieves a payment history record by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment history record retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Payment history record not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentHistoryResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getHistoryById(id)
        );
    }



    @GetMapping("/payment/{paymentId}")
    @Operation(
            summary = "Get Payment History By Payment Id",
            description = "Retrieves all payment history records associated with a payment id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment history records retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Payment or history records not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentHistoryResponse>>
    getByPaymentId(
            @PathVariable Long paymentId) {


        return ResponseEntity.ok(
                service.getHistoryByPaymentId(paymentId)
        );
    }



    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Payment History",
            description = "Deletes a payment history record by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment history record deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Payment history record not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> delete(
            @PathVariable Long id) {


        service.deleteHistory(id);

        return ResponseEntity.ok(
                "Payment history deleted successfully"
        );
    }
}