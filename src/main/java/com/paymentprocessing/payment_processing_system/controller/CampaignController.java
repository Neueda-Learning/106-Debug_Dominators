package com.paymentprocessing.payment_processing_system.controller;

import com.paymentprocessing.payment_processing_system.dto.CampaignRequest;
import com.paymentprocessing.payment_processing_system.dto.CampaignResponse;
import com.paymentprocessing.payment_processing_system.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/campaigns")
public class CampaignController {


    private final CampaignService campaignService;


    public CampaignController(
            CampaignService campaignService) {

        this.campaignService = campaignService;
    }



    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(
            @RequestBody CampaignRequest request) {


        return new ResponseEntity<>(
                campaignService.createCampaign(request),
                HttpStatus.CREATED
        );
    }



    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {


        return ResponseEntity.ok(
                campaignService.getAllCampaigns()
        );
    }



    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(
            @PathVariable Long id) {


        return ResponseEntity.ok(
                campaignService.getCampaignById(id)
        );
    }



    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable Long id,
            @RequestBody CampaignRequest request) {


        return ResponseEntity.ok(
                campaignService.updateCampaign(id, request)
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCampaign(
            @PathVariable Long id) {


        campaignService.deleteCampaign(id);


        return ResponseEntity.ok(
                "Campaign deleted successfully"
        );
    }
}