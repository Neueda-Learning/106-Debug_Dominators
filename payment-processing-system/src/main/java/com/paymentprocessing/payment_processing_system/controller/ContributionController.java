package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.ContributionRequest;
import com.paymentprocessing.payment_processing_system.dto.ContributionResponse;
import com.paymentprocessing.payment_processing_system.service.ContributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/contributions")
public class ContributionController {


    private final ContributionService contributionService;


    public ContributionController(
            ContributionService contributionService) {

        this.contributionService = contributionService;
    }



    @PostMapping
    public ResponseEntity<ContributionResponse> createContribution(
            @RequestBody ContributionRequest request) {


        return new ResponseEntity<>(
                contributionService.createContribution(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    public ResponseEntity<List<ContributionResponse>> getAllContributions() {


        return ResponseEntity.ok(
                contributionService.getAllContributions()
        );
    }



    @GetMapping("/{id}")
    public ResponseEntity<ContributionResponse> getContributionById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                contributionService.getContributionById(id)
        );
    }



    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<ContributionResponse>>
    getContributionsByCampaign(
            @PathVariable Long campaignId) {


        return ResponseEntity.ok(
                contributionService
                        .getContributionsByCampaign(campaignId)
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContribution(
            @PathVariable Long id) {


        contributionService.deleteContribution(id);


        return ResponseEntity.ok(
                "Contribution deleted successfully"
        );
    }
}