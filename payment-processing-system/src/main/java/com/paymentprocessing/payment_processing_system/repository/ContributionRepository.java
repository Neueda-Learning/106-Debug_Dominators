package com.paymentprocessing.payment_processing_system.repository;

import com.paymentprocessing.payment_processing_system.model.Contribution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionRepository
        extends CrudRepository<Contribution, Long> {


    List<Contribution> findByCampaignId(Long campaignId);


    List<Contribution> findByPaymentId(Long paymentId);

}