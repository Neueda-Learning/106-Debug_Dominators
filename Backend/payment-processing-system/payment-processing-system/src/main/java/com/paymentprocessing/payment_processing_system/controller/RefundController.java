package com.paymentprocessing.payment_processing_system.controller;

import com.paymentprocessing.payment_processing_system.dto.RefundRequest;
import com.paymentprocessing.payment_processing_system.dto.RefundResponse;
import com.paymentprocessing.payment_processing_system.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/refunds")
@Tag(name = "Refund APIs", description = "Operations for creating, managing, and retrieving refund transactions")
public class RefundController {

    private final RefundService refundService;


    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }


    @PostMapping
    @Operation(
            summary = "Create Refund",
            description = "Creates a new refund request for an existing payment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Refund created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Payment or refund resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RefundResponse> createRefund(
            @RequestBody RefundRequest refundRequest) {

        RefundResponse response =
                refundService.createRefund(refundRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get Refund By Id",
            description = "Retrieves a refund transaction by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Refund not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RefundResponse> getRefundById(
            @PathVariable Long id) {

        RefundResponse response =
                refundService.getRefundById(id);

        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Operation(
            summary = "Get All Refunds",
            description = "Retrieves all refund transactions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Refund resources not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<RefundResponse>> getAllRefunds() {

        return ResponseEntity.ok(
                refundService.getAllRefunds()
        );
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Update Refund",
            description = "Updates an existing refund transaction by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Refund not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RefundResponse> updateRefund(
            @PathVariable Long id,
            @RequestBody RefundRequest refundRequest) {

        RefundResponse response =
                refundService.updateRefund(id, refundRequest);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Refund",
            description = "Deletes a refund transaction by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Refund not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteRefund(
            @PathVariable Long id) {

        refundService.deleteRefund(id);

        return ResponseEntity.ok(
                "Refund deleted successfully."
        );
    }
}