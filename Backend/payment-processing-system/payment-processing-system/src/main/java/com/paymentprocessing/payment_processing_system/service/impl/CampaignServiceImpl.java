package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.CampaignRequest;
import com.paymentprocessing.payment_processing_system.dto.CampaignResponse;
import com.paymentprocessing.payment_processing_system.enums.CampaignStatus;
import com.paymentprocessing.payment_processing_system.exception.CampaignException;
import com.paymentprocessing.payment_processing_system.model.Campaign;
import com.paymentprocessing.payment_processing_system.repository.CampaignRepository;
import com.paymentprocessing.payment_processing_system.service.CampaignService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
public class CampaignServiceImpl implements CampaignService {


    private static final Logger log =
            LoggerFactory.getLogger(CampaignServiceImpl.class);



    private final CampaignRepository campaignRepository;



    public CampaignServiceImpl(
            CampaignRepository campaignRepository) {

        this.campaignRepository = campaignRepository;
    }





    @Override
    public CampaignResponse createCampaign(
            CampaignRequest request) {


        log.info(
                "Creating campaign with title: {}",
                request.getCampaignTitle()
        );


        Campaign campaign = new Campaign();


        campaign.setCampaignCode(
                "CMP" + System.currentTimeMillis()
        );


        campaign.setCampaignTitle(
                request.getCampaignTitle()
        );


        campaign.setOrganizerName(
                request.getOrganizerName()
        );


        campaign.setCategory(
                request.getCategory()
        );


        campaign.setGoalAmount(
                request.getGoalAmount()
        );


        campaign.setCollectedAmount(
                BigDecimal.ZERO
        );


        campaign.setStartDate(
                request.getStartDate()
        );


        campaign.setEndDate(
                request.getEndDate()
        );


        campaign.setCampaignStatus(
                CampaignStatus.ACTIVE
        );


        campaign.setDescription(
                request.getDescription()
        );


        campaign.setCreatedBy(
                request.getCreatedBy()
        );


        campaign.setCreatedAt(
                LocalDateTime.now()
        );



        Campaign savedCampaign =
                campaignRepository.save(campaign);



        log.info(
                "Campaign created successfully with id: {}",
                savedCampaign.getCampaignId()
        );


        return mapToResponse(savedCampaign);
    }





    @Override
    public CampaignResponse getCampaignById(Long id) {


        log.info(
                "Fetching campaign with id: {}",
                id
        );


        Campaign campaign =
                campaignRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Campaign not found with id: {}",
                                    id
                            );


                            return new CampaignException(
                                    "Campaign not found with id: " + id
                            );
                        });



        return mapToResponse(campaign);
    }





    @Override
    public List<CampaignResponse> getAllCampaigns() {


        log.info("Fetching all campaigns");


        List<CampaignResponse> responses =
                new ArrayList<>();


        campaignRepository.findAll()
                .forEach(campaign ->
                        responses.add(
                                mapToResponse(campaign)
                        ));


        return responses;
    }





    @Override
    public CampaignResponse updateCampaign(
            Long id,
            CampaignRequest request) {


        log.info(
                "Updating campaign with id: {}",
                id
        );


        Campaign campaign =
                campaignRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Campaign not found for update with id: {}",
                                    id
                            );


                            return new CampaignException(
                                    "Campaign not found with id: " + id
                            );
                        });



        campaign.setCampaignTitle(
                request.getCampaignTitle()
        );


        campaign.setOrganizerName(
                request.getOrganizerName()
        );


        campaign.setCategory(
                request.getCategory()
        );


        campaign.setGoalAmount(
                request.getGoalAmount()
        );


        campaign.setStartDate(
                request.getStartDate()
        );


        campaign.setEndDate(
                request.getEndDate()
        );


        campaign.setDescription(
                request.getDescription()
        );



        Campaign updated =
                campaignRepository.save(campaign);



        log.info(
                "Campaign updated successfully with id: {}",
                updated.getCampaignId()
        );


        return mapToResponse(updated);
    }





    @Override
    public void deleteCampaign(Long id) {


        log.info(
                "Deleting campaign with id: {}",
                id
        );


        if (!campaignRepository.existsById(id)) {


            log.error(
                    "Campaign not found for deletion with id: {}",
                    id
            );


            throw new CampaignException(
                    "Campaign not found with id: " + id
            );
        }



        campaignRepository.deleteById(id);



        log.info(
                "Campaign deleted successfully with id: {}",
                id
        );
    }





    private CampaignResponse mapToResponse(
            Campaign campaign) {


        CampaignResponse response =
                new CampaignResponse();


        response.setCampaignId(
                campaign.getCampaignId()
        );


        response.setCampaignCode(
                campaign.getCampaignCode()
        );


        response.setCampaignTitle(
                campaign.getCampaignTitle()
        );


        response.setOrganizerName(
                campaign.getOrganizerName()
        );


        response.setCategory(
                campaign.getCategory()
        );


        response.setGoalAmount(
                campaign.getGoalAmount()
        );


        response.setCollectedAmount(
                campaign.getCollectedAmount()
        );


        response.setStartDate(
                campaign.getStartDate()
        );


        response.setEndDate(
                campaign.getEndDate()
        );


        response.setCampaignStatus(
                campaign.getCampaignStatus()
        );


        response.setDescription(
                campaign.getDescription()
        );


        response.setCreatedAt(
                campaign.getCreatedAt()
        );


        response.setCreatedBy(
                campaign.getCreatedBy()
        );


        return response;
    }
}