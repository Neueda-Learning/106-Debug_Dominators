package com.paymentprocessing.payment_processing_system.dto;

import com.paymentprocessing.payment_processing_system.enums.ContributionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContributionResponse {

    private Long contributionId;

    private Long campaignId;

    private Long paymentId;

    private String contributorName;

    private String contributorEmail;

    private BigDecimal contributionAmount;

    private ContributionStatus contributionStatus;

    private Boolean anonymousDonation;

    private String message;

    private String receiptNumber;

    private LocalDateTime contributionDate;


    public ContributionResponse() {
    }


    public Long getContributionId() {
        return contributionId;
    }

    public void setContributionId(Long contributionId) {
        this.contributionId = contributionId;
    }


    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }


    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }


    public String getContributorEmail() {
        return contributorEmail;
    }

    public void setContributorEmail(String contributorEmail) {
        this.contributorEmail = contributorEmail;
    }


    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }


    public ContributionStatus getContributionStatus() {
        return contributionStatus;
    }

    public void setContributionStatus(ContributionStatus contributionStatus) {
        this.contributionStatus = contributionStatus;
    }


    public Boolean getAnonymousDonation() {
        return anonymousDonation;
    }

    public void setAnonymousDonation(Boolean anonymousDonation) {
        this.anonymousDonation = anonymousDonation;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }


    public LocalDateTime getContributionDate() {
        return contributionDate;
    }

    public void setContributionDate(LocalDateTime contributionDate) {
        this.contributionDate = contributionDate;
    }
}