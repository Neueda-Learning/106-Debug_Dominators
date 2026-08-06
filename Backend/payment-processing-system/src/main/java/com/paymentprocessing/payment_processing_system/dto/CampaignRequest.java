package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.CampaignCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CampaignRequest {

    private String campaignTitle;

    private String organizerName;

    private CampaignCategory category;

    private BigDecimal goalAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private String createdBy;


    public CampaignRequest() {
    }


    public CampaignRequest(String campaignTitle,
                           String organizerName,
                           CampaignCategory category,
                           BigDecimal goalAmount,
                           LocalDate startDate,
                           LocalDate endDate,
                           String description,
                           String createdBy) {

        this.campaignTitle = campaignTitle;
        this.organizerName = organizerName;
        this.category = category;
        this.goalAmount = goalAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.createdBy = createdBy;
    }


    public String getCampaignTitle() {
        return campaignTitle;
    }

    public void setCampaignTitle(String campaignTitle) {
        this.campaignTitle = campaignTitle;
    }


    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }


    public CampaignCategory getCategory() {
        return category;
    }

    public void setCategory(CampaignCategory category) {
        this.category = category;
    }


    public BigDecimal getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(BigDecimal goalAmount) {
        this.goalAmount = goalAmount;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}