package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.service.StatementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/statements")
@Tag(
        name = "Statement APIs",
        description = "APIs for generating payment transaction statements"
)
public class StatementController {


    private final StatementService statementService;


    public StatementController(
            StatementService statementService) {

        this.statementService = statementService;
    }



    @GetMapping("/payment/{paymentId}")
    @Operation(
            summary = "Generate payment statement",
            description = "Generates a PDF transaction statement for a payment"
    )
    public ResponseEntity<Resource> generateStatement(
            @PathVariable Long paymentId) {


        Resource resource =
                statementService.generatePaymentStatement(paymentId);


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=payment_statement.pdf"
                )
                .body(resource);
    }
}