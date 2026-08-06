package com.paymentprocessing.payment_processing_system.controller;


import com.paymentprocessing.payment_processing_system.dto.ContributionRequest;
import com.paymentprocessing.payment_processing_system.dto.ContributionResponse;
import com.paymentprocessing.payment_processing_system.service.ContributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/contributions")
@Tag(name = "Contribution APIs", description = "Operations for creating and managing campaign contributions")
public class ContributionController {


    private final ContributionService contributionService;


    public ContributionController(
            ContributionService contributionService) {

        this.contributionService = contributionService;
    }



    @PostMapping
    @Operation(
            summary = "Create Contribution",
            description = "Creates a new contribution linked to a campaign"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contribution created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Campaign or contribution resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ContributionResponse> createContribution(
            @RequestBody ContributionRequest request) {


        return new ResponseEntity<>(
                contributionService.createContribution(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    @Operation(
            summary = "Get All Contributions",
            description = "Retrieves all contribution records"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contributions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Contribution resources not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ContributionResponse>> getAllContributions() {


        return ResponseEntity.ok(
                contributionService.getAllContributions()
        );
    }



    @GetMapping("/{id}")
    @Operation(
            summary = "Get Contribution By Id",
            description = "Retrieves a contribution by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contribution retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Contribution not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ContributionResponse> getContributionById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                contributionService.getContributionById(id)
        );
    }



    @GetMapping("/campaign/{campaignId}")
    @Operation(
            summary = "Get Contributions By Campaign",
            description = "Retrieves all contributions for a specific campaign id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contributions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Campaign not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ContributionResponse>>
    getContributionsByCampaign(
            @PathVariable Long campaignId) {


        return ResponseEntity.ok(
                contributionService
                        .getContributionsByCampaign(campaignId)
        );
    }



    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Contribution",
            description = "Deletes a contribution by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contribution deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Contribution not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteContribution(
            @PathVariable Long id) {


        contributionService.deleteContribution(id);


        return ResponseEntity.ok(
                "Contribution deleted successfully"
        );
    }
}