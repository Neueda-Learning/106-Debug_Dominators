package com.paymentprocessing.payment_processing_system.controller;

import com.paymentprocessing.payment_processing_system.dto.CampaignRequest;
import com.paymentprocessing.payment_processing_system.dto.CampaignResponse;
import com.paymentprocessing.payment_processing_system.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/campaigns")
@Tag(name = "Campaign APIs", description = "Operations for creating, updating, and managing campaigns")
public class CampaignController {


    private final CampaignService campaignService;


    public CampaignController(
            CampaignService campaignService) {

        this.campaignService = campaignService;
    }



    @PostMapping
    @Operation(
            summary = "Create Campaign",
            description = "Creates a new fundraising campaign"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Campaign created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Related resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CampaignResponse> createCampaign(
            @RequestBody CampaignRequest request) {


        return new ResponseEntity<>(
                campaignService.createCampaign(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    @Operation(
            summary = "Get All Campaigns",
            description = "Retrieves all campaigns"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaigns retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Campaign resources not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {


        return ResponseEntity.ok(
                campaignService.getAllCampaigns()
        );
    }



    @GetMapping("/{id}")
    @Operation(
            summary = "Get Campaign By Id",
            description = "Retrieves a campaign by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaign retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Campaign not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CampaignResponse> getCampaignById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                campaignService.getCampaignById(id)
        );
    }



    @PutMapping("/{id}")
    @Operation(
            summary = "Update Campaign",
            description = "Updates an existing campaign by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaign updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or validation failure"),
            @ApiResponse(responseCode = "404", description = "Campaign not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable Long id,
            @RequestBody CampaignRequest request) {


        return ResponseEntity.ok(
                campaignService.updateCampaign(id, request)
        );
    }



    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Campaign",
            description = "Deletes a campaign by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaign deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Campaign not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteCampaign(
            @PathVariable Long id) {


        campaignService.deleteCampaign(id);


        return ResponseEntity.ok(
                "Campaign deleted successfully"
        );
    }
}
