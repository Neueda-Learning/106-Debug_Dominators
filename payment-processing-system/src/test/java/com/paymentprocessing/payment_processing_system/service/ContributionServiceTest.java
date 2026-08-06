package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.ContributionRequest;
import com.paymentprocessing.payment_processing_system.dto.ContributionResponse;
import com.paymentprocessing.payment_processing_system.enums.ContributionStatus;
import com.paymentprocessing.payment_processing_system.exception.ContributionException;
import com.paymentprocessing.payment_processing_system.model.Campaign;
import com.paymentprocessing.payment_processing_system.model.Contribution;
import com.paymentprocessing.payment_processing_system.repository.CampaignRepository;
import com.paymentprocessing.payment_processing_system.repository.ContributionRepository;
import com.paymentprocessing.payment_processing_system.service.impl.ContributionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContributionServiceTest {

    @Mock
    private ContributionRepository contributionRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private ContributionServiceImpl contributionService;

    @Test
    void createContribution_shouldCreateContributionAndSaveToRepository() {
        ContributionRequest request = buildContributionRequest();

        Campaign campaign = new Campaign();
        campaign.setCampaignId(request.getCampaignId());
        campaign.setCollectedAmount(BigDecimal.ZERO);
        when(campaignRepository.findById(request.getCampaignId())).thenReturn(Optional.of(campaign));

        when(contributionRepository.save(any(Contribution.class))).thenAnswer(invocation -> {
            Contribution contribution = invocation.getArgument(0);
            contribution.setContributionId(1L);
            return contribution;
        });

        ContributionResponse response = contributionService.createContribution(request);

        ArgumentCaptor<Contribution> contributionCaptor = ArgumentCaptor.forClass(Contribution.class);
        verify(contributionRepository).save(contributionCaptor.capture());

        Contribution savedEntity = contributionCaptor.getValue();
        assertThat(savedEntity.getContributionId()).isEqualTo(1L);
        assertThat(savedEntity.getCampaignId()).isEqualTo(request.getCampaignId());
        assertThat(savedEntity.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(savedEntity.getContributorName()).isEqualTo(request.getContributorName());
        assertThat(savedEntity.getContributorEmail()).isEqualTo(request.getContributorEmail());
        assertThat(savedEntity.getContributionAmount()).isEqualByComparingTo(request.getContributionAmount());
        assertThat(savedEntity.getAnonymousDonation()).isEqualTo(request.getAnonymousDonation());
        assertThat(savedEntity.getMessage()).isEqualTo(request.getMessage());
        assertThat(savedEntity.getContributionStatus()).isEqualTo(ContributionStatus.PENDING);
        assertThat(savedEntity.getContributionDate()).isNotNull();
        assertThat(savedEntity.getReceiptNumber()).startsWith("REC");

        assertThat(response.getContributionId()).isEqualTo(1L);
        assertThat(response.getCampaignId()).isEqualTo(request.getCampaignId());
        assertThat(response.getPaymentId()).isEqualTo(request.getPaymentId());
        assertThat(response.getContributorName()).isEqualTo(request.getContributorName());
        assertThat(response.getContributorEmail()).isEqualTo(request.getContributorEmail());
        assertThat(response.getContributionAmount()).isEqualByComparingTo(request.getContributionAmount());
        assertThat(response.getAnonymousDonation()).isEqualTo(request.getAnonymousDonation());
        assertThat(response.getMessage()).isEqualTo(request.getMessage());
        assertThat(response.getContributionStatus()).isEqualTo(ContributionStatus.PENDING);
        assertThat(response.getReceiptNumber()).isEqualTo(savedEntity.getReceiptNumber());
        assertThat(response.getContributionDate()).isNotNull();
    }

    @Test
    void getContributionById_shouldReturnContributionWhenExists() {
        Long contributionId = 10L;
        Contribution contribution = buildContributionEntity(contributionId);
        when(contributionRepository.findById(contributionId)).thenReturn(Optional.of(contribution));

        ContributionResponse response = contributionService.getContributionById(contributionId);

        verify(contributionRepository).findById(contributionId);
        assertThat(response.getContributionId()).isEqualTo(contribution.getContributionId());
        assertThat(response.getCampaignId()).isEqualTo(contribution.getCampaignId());
        assertThat(response.getPaymentId()).isEqualTo(contribution.getPaymentId());
        assertThat(response.getContributionStatus()).isEqualTo(contribution.getContributionStatus());
    }

    @Test
    void getContributionById_whenNotFound_shouldThrowContributionException() {
        Long contributionId = 99L;
        when(contributionRepository.findById(contributionId)).thenReturn(Optional.empty());

        ContributionException exception = assertThrows(
                ContributionException.class,
                () -> contributionService.getContributionById(contributionId)
        );

        verify(contributionRepository).findById(contributionId);
        assertThat(exception.getMessage()).isEqualTo("Contribution not found with id: 99");
    }

    @Test
    void deleteContribution_shouldDeleteContributionWhenExists() {
        Long contributionId = 5L;
        when(contributionRepository.existsById(contributionId)).thenReturn(true);

        contributionService.deleteContribution(contributionId);

        verify(contributionRepository).existsById(contributionId);
        verify(contributionRepository).deleteById(contributionId);
    }

    @Test
    void deleteContribution_whenNotFound_shouldThrowContributionException() {
        Long contributionId = 6L;
        when(contributionRepository.existsById(contributionId)).thenReturn(false);

        ContributionException exception = assertThrows(
                ContributionException.class,
                () -> contributionService.deleteContribution(contributionId)
        );

        verify(contributionRepository).existsById(contributionId);
        verify(contributionRepository, never()).deleteById(contributionId);
        assertThat(exception.getMessage()).isEqualTo("Contribution not found with id: 6");
    }

    @Test
    void createContribution_whenRepositorySaveFails_shouldPropagateException() {
        ContributionRequest request = buildContributionRequest();
        when(contributionRepository.save(any(Contribution.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> contributionService.createContribution(request)
        );

        verify(contributionRepository).save(any(Contribution.class));
        assertThat(exception.getMessage()).isEqualTo("Database error");
    }

    private ContributionRequest buildContributionRequest() {
        ContributionRequest request = new ContributionRequest();
        request.setCampaignId(1001L);
        request.setPaymentId(2002L);
        request.setContributorName("Alice Donor");
        request.setContributorEmail("alice@example.com");
        request.setContributionAmount(new BigDecimal("75.50"));
        request.setAnonymousDonation(Boolean.FALSE);
        request.setMessage("Keep up the great work");
        return request;
    }

    private Contribution buildContributionEntity(Long id) {
        Contribution contribution = new Contribution();
        contribution.setContributionId(id);
        contribution.setCampaignId(1001L);
        contribution.setPaymentId(2002L);
        contribution.setContributorName("Alice Donor");
        contribution.setContributorEmail("alice@example.com");
        contribution.setContributionAmount(new BigDecimal("75.50"));
        contribution.setAnonymousDonation(Boolean.FALSE);
        contribution.setMessage("Keep up the great work");
        contribution.setContributionStatus(ContributionStatus.PENDING);
        contribution.setReceiptNumber("REC123456");
        contribution.setContributionDate(LocalDateTime.now());
        return contribution;
    }
}

