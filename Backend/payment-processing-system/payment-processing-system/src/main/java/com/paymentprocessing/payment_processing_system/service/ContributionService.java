package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.ContributionRequest;
import com.paymentprocessing.payment_processing_system.dto.ContributionResponse;

import java.util.List;

public interface ContributionService {

    ContributionResponse createContribution(ContributionRequest request);

    ContributionResponse getContributionById(Long id);

    List<ContributionResponse> getAllContributions();

    List<ContributionResponse> getContributionsByCampaign(Long campaignId);

    void deleteContribution(Long id);

}