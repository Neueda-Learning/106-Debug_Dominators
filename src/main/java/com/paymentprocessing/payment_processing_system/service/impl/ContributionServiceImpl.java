package com.paymentprocessing.payment_processing_system.service.impl;


import com.paymentprocessing.payment_processing_system.dto.ContributionRequest;
import com.paymentprocessing.payment_processing_system.dto.ContributionResponse;
import com.paymentprocessing.payment_processing_system.enums.ContributionStatus;
import com.paymentprocessing.payment_processing_system.enums.PaymentStatus;
import com.paymentprocessing.payment_processing_system.exception.ContributionException;
import com.paymentprocessing.payment_processing_system.model.Campaign;
import com.paymentprocessing.payment_processing_system.model.Contribution;
import com.paymentprocessing.payment_processing_system.model.Payment;
import com.paymentprocessing.payment_processing_system.repository.CampaignRepository;
import com.paymentprocessing.payment_processing_system.repository.ContributionRepository;
import com.paymentprocessing.payment_processing_system.repository.PaymentRepository;
import com.paymentprocessing.payment_processing_system.service.ContributionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
public class ContributionServiceImpl implements ContributionService {


    private static final Logger log =
            LoggerFactory.getLogger(ContributionServiceImpl.class);



    private final ContributionRepository contributionRepository;

    private final PaymentRepository paymentRepository;

    private final CampaignRepository campaignRepository;



    public ContributionServiceImpl(
            ContributionRepository contributionRepository,
            PaymentRepository paymentRepository,
            CampaignRepository campaignRepository) {

        this.contributionRepository = contributionRepository;
        this.paymentRepository = paymentRepository;
        this.campaignRepository = campaignRepository;
    }





    @Override
    public ContributionResponse createContribution(
            ContributionRequest request) {


        log.info(
                "Creating contribution for campaign id: {}",
                request.getCampaignId()
        );


        Contribution contribution = new Contribution();


        contribution.setCampaignId(
                request.getCampaignId()
        );


        contribution.setPaymentId(
                request.getPaymentId()
        );


        contribution.setContributorName(
                request.getContributorName()
        );


        contribution.setContributorEmail(
                request.getContributorEmail()
        );


        contribution.setContributionAmount(
                request.getContributionAmount()
        );


        contribution.setAnonymousDonation(
                request.getAnonymousDonation()
        );


        contribution.setMessage(
                request.getMessage()
        );


        ContributionStatus status =
                resolveContributionStatus(request.getPaymentId());

        contribution.setContributionStatus(status);


        contribution.setContributionDate(
                LocalDateTime.now()
        );


        contribution.setReceiptNumber(
                "REC" + System.currentTimeMillis()
        );



        Contribution savedContribution =
                contributionRepository.save(contribution);



        if (status == ContributionStatus.SUCCESS) {
            creditCampaign(
                    request.getCampaignId(),
                    request.getContributionAmount()
            );
        }



        log.info(
                "Contribution created successfully with id: {}",
                savedContribution.getContributionId()
        );


        return mapToResponse(savedContribution);
    }





    /**
     * Mirrors the linked payment's final status onto the contribution so it
     * doesn't stay stuck as PENDING once the payment has completed or failed.
     */
    private ContributionStatus resolveContributionStatus(Long paymentId) {

        if (paymentId == null) {
            return ContributionStatus.PENDING;
        }

        return paymentRepository.findById(paymentId)
                .map(Payment::getStatus)
                .map(this::mapPaymentStatus)
                .orElse(ContributionStatus.PENDING);
    }



    private ContributionStatus mapPaymentStatus(PaymentStatus paymentStatus) {

        if (paymentStatus == PaymentStatus.COMPLETED) {
            return ContributionStatus.SUCCESS;
        }

        if (paymentStatus == PaymentStatus.FAILED) {
            return ContributionStatus.FAILED;
        }

        return ContributionStatus.PENDING;
    }



    private void creditCampaign(Long campaignId, BigDecimal amount) {

        if (campaignId == null || amount == null) {
            return;
        }

        campaignRepository.findById(campaignId).ifPresent(campaign -> {

            BigDecimal current =
                    campaign.getCollectedAmount() != null
                            ? campaign.getCollectedAmount()
                            : BigDecimal.ZERO;

            campaign.setCollectedAmount(current.add(amount));

            campaignRepository.save(campaign);
        });
    }





    @Override
    public ContributionResponse getContributionById(Long id) {


        log.info(
                "Fetching contribution with id: {}",
                id
        );



        Contribution contribution =
                contributionRepository.findById(id)
                        .orElseThrow(() -> {


                            log.error(
                                    "Contribution not found with id: {}",
                                    id
                            );


                            return new ContributionException(
                                    "Contribution not found with id: "
                                            + id
                            );
                        });



        return mapToResponse(contribution);
    }





    @Override
    public List<ContributionResponse> getAllContributions() {


        log.info("Fetching all contributions");


        List<ContributionResponse> responses =
                new ArrayList<>();


        contributionRepository.findAll()
                .forEach(contribution ->
                        responses.add(
                                mapToResponse(contribution)
                        ));


        return responses;
    }





    @Override
    public List<ContributionResponse> getContributionsByCampaign(
            Long campaignId) {


        log.info(
                "Fetching contributions for campaign id: {}",
                campaignId
        );


        List<ContributionResponse> responses =
                new ArrayList<>();


        contributionRepository.findByCampaignId(campaignId)
                .forEach(contribution ->
                        responses.add(
                                mapToResponse(contribution)
                        ));


        return responses;
    }





    @Override
    public void deleteContribution(Long id) {


        log.info(
                "Deleting contribution with id: {}",
                id
        );


        if (!contributionRepository.existsById(id)) {


            log.error(
                    "Contribution not found for deletion with id: {}",
                    id
            );


            throw new ContributionException(
                    "Contribution not found with id: " + id
            );
        }



        contributionRepository.deleteById(id);



        log.info(
                "Contribution deleted successfully with id: {}",
                id
        );
    }





    private ContributionResponse mapToResponse(
            Contribution contribution) {


        ContributionResponse response =
                new ContributionResponse();


        response.setContributionId(
                contribution.getContributionId()
        );


        response.setCampaignId(
                contribution.getCampaignId()
        );


        response.setPaymentId(
                contribution.getPaymentId()
        );


        response.setContributorName(
                contribution.getContributorName()
        );


        response.setContributorEmail(
                contribution.getContributorEmail()
        );


        response.setContributionAmount(
                contribution.getContributionAmount()
        );


        response.setContributionStatus(
                contribution.getContributionStatus()
        );


        response.setAnonymousDonation(
                contribution.getAnonymousDonation()
        );


        response.setMessage(
                contribution.getMessage()
        );


        response.setReceiptNumber(
                contribution.getReceiptNumber()
        );


        response.setContributionDate(
                contribution.getContributionDate()
        );


        return response;
    }
}