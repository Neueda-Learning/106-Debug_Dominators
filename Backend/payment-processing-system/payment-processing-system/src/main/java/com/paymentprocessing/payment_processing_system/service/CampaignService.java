package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.CampaignRequest;
import com.paymentprocessing.payment_processing_system.dto.CampaignResponse;

import java.util.List;

public interface CampaignService {

    CampaignResponse createCampaign(CampaignRequest request);

    CampaignResponse getCampaignById(Long id);

    List<CampaignResponse> getAllCampaigns();

    CampaignResponse updateCampaign(Long id, CampaignRequest request);

    void deleteCampaign(Long id);

}