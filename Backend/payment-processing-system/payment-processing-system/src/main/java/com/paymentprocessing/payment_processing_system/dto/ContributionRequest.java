package com.paymentprocessing.payment_processing_system.dto;

import java.math.BigDecimal;

public class ContributionRequest {

    private Long campaignId;

    private Long paymentId;

    private String contributorName;

    private String contributorEmail;

    private BigDecimal contributionAmount;

    private Boolean anonymousDonation;

    private String message;


    public ContributionRequest() {
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
}