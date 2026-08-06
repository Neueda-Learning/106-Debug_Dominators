package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.CampaignRequest;
import com.paymentprocessing.payment_processing_system.dto.CampaignResponse;
import com.paymentprocessing.payment_processing_system.enums.CampaignCategory;
import com.paymentprocessing.payment_processing_system.enums.CampaignStatus;
import com.paymentprocessing.payment_processing_system.exception.CampaignException;
import com.paymentprocessing.payment_processing_system.model.Campaign;
import com.paymentprocessing.payment_processing_system.repository.CampaignRepository;
import com.paymentprocessing.payment_processing_system.service.impl.CampaignServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Test
    void createCampaign_shouldCreateCampaignAndSaveToRepository() {
        CampaignRequest request = buildCampaignRequest();

        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign campaign = invocation.getArgument(0);
            campaign.setCampaignId(1L);
            return campaign;
        });

        CampaignResponse response = campaignService.createCampaign(request);

        ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);
        verify(campaignRepository).save(campaignCaptor.capture());

        Campaign savedEntity = campaignCaptor.getValue();
        assertThat(savedEntity.getCampaignId()).isEqualTo(1L);
        assertThat(savedEntity.getCampaignCode()).startsWith("CMP");
        assertThat(savedEntity.getCampaignTitle()).isEqualTo(request.getCampaignTitle());
        assertThat(savedEntity.getOrganizerName()).isEqualTo(request.getOrganizerName());
        assertThat(savedEntity.getCategory()).isEqualTo(request.getCategory());
        assertThat(savedEntity.getGoalAmount()).isEqualByComparingTo(request.getGoalAmount());
        assertThat(savedEntity.getCollectedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(savedEntity.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(savedEntity.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(savedEntity.getCampaignStatus()).isEqualTo(CampaignStatus.ACTIVE);
        assertThat(savedEntity.getDescription()).isEqualTo(request.getDescription());
        assertThat(savedEntity.getCreatedBy()).isEqualTo(request.getCreatedBy());
        assertThat(savedEntity.getCreatedAt()).isNotNull();

        assertThat(response.getCampaignId()).isEqualTo(1L);
        assertThat(response.getCampaignCode()).isEqualTo(savedEntity.getCampaignCode());
        assertThat(response.getCampaignTitle()).isEqualTo(request.getCampaignTitle());
        assertThat(response.getOrganizerName()).isEqualTo(request.getOrganizerName());
        assertThat(response.getCategory()).isEqualTo(request.getCategory());
        assertThat(response.getGoalAmount()).isEqualByComparingTo(request.getGoalAmount());
        assertThat(response.getCollectedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getCampaignStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    void getCampaignById_shouldReturnCampaignWhenExists() {
        Long campaignId = 10L;
        Campaign campaign = buildCampaignEntity(campaignId);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        CampaignResponse response = campaignService.getCampaignById(campaignId);

        verify(campaignRepository).findById(campaignId);
        assertThat(response.getCampaignId()).isEqualTo(campaign.getCampaignId());
        assertThat(response.getCampaignCode()).isEqualTo(campaign.getCampaignCode());
        assertThat(response.getCampaignStatus()).isEqualTo(campaign.getCampaignStatus());
    }

    @Test
    void getCampaignById_whenNotFound_shouldThrowCampaignException() {
        Long campaignId = 99L;
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        CampaignException exception = assertThrows(
                CampaignException.class,
                () -> campaignService.getCampaignById(campaignId)
        );

        verify(campaignRepository).findById(campaignId);
        assertThat(exception.getMessage()).isEqualTo("Campaign not found with id: 99");
    }

    @Test
    void updateCampaign_shouldUpdateAndReturnUpdatedCampaignWhenExists() {
        Long campaignId = 1L;
        Campaign existingCampaign = buildCampaignEntity(campaignId);
        CampaignRequest updateRequest = new CampaignRequest(
                "New Campaign Title",
                "Jane Organizer",
                CampaignCategory.EDUCATION,
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 12, 31),
                "Updated campaign description",
                "admin-user"
        );

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(existingCampaign));
        when(campaignRepository.save(existingCampaign)).thenReturn(existingCampaign);

        CampaignResponse response = campaignService.updateCampaign(campaignId, updateRequest);

        verify(campaignRepository).findById(campaignId);
        verify(campaignRepository).save(existingCampaign);

        assertThat(existingCampaign.getCampaignTitle()).isEqualTo("New Campaign Title");
        assertThat(existingCampaign.getOrganizerName()).isEqualTo("Jane Organizer");
        assertThat(existingCampaign.getCategory()).isEqualTo(CampaignCategory.EDUCATION);
        assertThat(existingCampaign.getGoalAmount()).isEqualByComparingTo("25000.00");
        assertThat(existingCampaign.getStartDate()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(existingCampaign.getEndDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(existingCampaign.getDescription()).isEqualTo("Updated campaign description");

        assertThat(response.getCampaignId()).isEqualTo(campaignId);
        assertThat(response.getCampaignTitle()).isEqualTo("New Campaign Title");
        assertThat(response.getOrganizerName()).isEqualTo("Jane Organizer");
        assertThat(response.getCampaignStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    void updateCampaign_whenNotFound_shouldThrowCampaignException() {
        Long campaignId = 2L;
        CampaignRequest updateRequest = buildCampaignRequest();
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        CampaignException exception = assertThrows(
                CampaignException.class,
                () -> campaignService.updateCampaign(campaignId, updateRequest)
        );

        verify(campaignRepository).findById(campaignId);
        verify(campaignRepository, never()).save(any(Campaign.class));
        assertThat(exception.getMessage()).isEqualTo("Campaign not found with id: 2");
    }

    @Test
    void deleteCampaign_shouldDeleteCampaignWhenExists() {
        Long campaignId = 5L;
        when(campaignRepository.existsById(campaignId)).thenReturn(true);

        campaignService.deleteCampaign(campaignId);

        verify(campaignRepository).existsById(campaignId);
        verify(campaignRepository).deleteById(campaignId);
    }

    @Test
    void deleteCampaign_whenNotFound_shouldThrowCampaignException() {
        Long campaignId = 6L;
        when(campaignRepository.existsById(campaignId)).thenReturn(false);

        CampaignException exception = assertThrows(
                CampaignException.class,
                () -> campaignService.deleteCampaign(campaignId)
        );

        verify(campaignRepository).existsById(campaignId);
        verify(campaignRepository, never()).deleteById(campaignId);
        assertThat(exception.getMessage()).isEqualTo("Campaign not found with id: 6");
    }

    @Test
    void createCampaign_whenRepositorySaveFails_shouldPropagateException() {
        CampaignRequest request = buildCampaignRequest();
        when(campaignRepository.save(any(Campaign.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> campaignService.createCampaign(request)
        );

        verify(campaignRepository).save(any(Campaign.class));
        assertThat(exception.getMessage()).isEqualTo("Database error");
    }

    @Test
    void updateCampaign_whenRepositorySaveFails_shouldPropagateException() {
        Long campaignId = 3L;
        Campaign existingCampaign = buildCampaignEntity(campaignId);
        CampaignRequest updateRequest = buildCampaignRequest();

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(existingCampaign));
        when(campaignRepository.save(existingCampaign)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> campaignService.updateCampaign(campaignId, updateRequest)
        );

        verify(campaignRepository).findById(campaignId);
        verify(campaignRepository).save(existingCampaign);
        assertThat(exception.getMessage()).isEqualTo("Database error");
    }

    private CampaignRequest buildCampaignRequest() {
        return new CampaignRequest(
                "Help Children",
                "John Organizer",
                CampaignCategory.MEDICAL,
                new BigDecimal("10000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                "Campaign for healthcare support",
                "system-admin"
        );
    }

    private Campaign buildCampaignEntity(Long id) {
        Campaign campaign = new Campaign();
        campaign.setCampaignId(id);
        campaign.setCampaignCode("CMP123456");
        campaign.setCampaignTitle("Help Children");
        campaign.setOrganizerName("John Organizer");
        campaign.setCategory(CampaignCategory.MEDICAL);
        campaign.setGoalAmount(new BigDecimal("10000.00"));
        campaign.setCollectedAmount(BigDecimal.ZERO);
        campaign.setStartDate(LocalDate.of(2026, 1, 1));
        campaign.setEndDate(LocalDate.of(2026, 6, 30));
        campaign.setCampaignStatus(CampaignStatus.ACTIVE);
        campaign.setDescription("Campaign for healthcare support");
        campaign.setCreatedBy("system-admin");
        campaign.setCreatedAt(LocalDateTime.now());
        return campaign;
    }
}

