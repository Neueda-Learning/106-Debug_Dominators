package com.paymentprocessing.payment_processing_system.controller;

import com.paymentprocessing.payment_processing_system.dto.RefundRequest;
import com.paymentprocessing.payment_processing_system.dto.RefundResponse;
import com.paymentprocessing.payment_processing_system.service.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/refunds")
public class RefundController {

    private final RefundService refundService;


    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }


    @PostMapping
    public ResponseEntity<RefundResponse> createRefund(
            @RequestBody RefundRequest refundRequest) {

        RefundResponse response =
                refundService.createRefund(refundRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RefundResponse> getRefundById(
            @PathVariable Long id) {

        RefundResponse response =
                refundService.getRefundById(id);

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<RefundResponse>> getAllRefunds() {

        return ResponseEntity.ok(
                refundService.getAllRefunds()
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<RefundResponse> updateRefund(
            @PathVariable Long id,
            @RequestBody RefundRequest refundRequest) {

        RefundResponse response =
                refundService.updateRefund(id, refundRequest);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRefund(
            @PathVariable Long id) {

        refundService.deleteRefund(id);

        return ResponseEntity.ok(
                "Refund deleted successfully."
        );
    }
}