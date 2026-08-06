package com.paymentprocessing.payment_processing_system.service;

import com.paymentprocessing.payment_processing_system.dto.RefundRequest;
import com.paymentprocessing.payment_processing_system.dto.RefundResponse;

import java.util.List;

public interface RefundService {

    RefundResponse createRefund(RefundRequest refundRequest);

    RefundResponse getRefundById(Long refundId);

    List<RefundResponse> getAllRefunds();

    RefundResponse updateRefund(Long refundId, RefundRequest refundRequest);

    void deleteRefund(Long refundId);

}