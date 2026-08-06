package com.paymentprocessing.payment_processing_system.service;

import org.springframework.core.io.Resource;


public interface StatementService {


    Resource generatePaymentStatement(Long paymentId);

}