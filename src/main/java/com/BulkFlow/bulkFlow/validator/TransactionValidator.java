package com.BulkFlow.bulkFlow.validator;

import com.BulkFlow.bulkFlow.dto.TransactionRecordDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TransactionValidator {

    public String validate(TransactionRecordDto dto) {

        if (dto.getTransactionId() == null || dto.getTransactionId().isBlank()) {
            return "transactionId is required";
        }

        if (dto.getLoanAccountNo() == null || dto.getLoanAccountNo().isBlank()) {
            return "loanAccountNo is required";
        }

        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            return "customerName is required";
        }

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            return "amount should be greater than 0";
        }

        if (!List.of("SUCCESS", "FAILED", "PENDING").contains(dto.getStatus())) {
            return "invalid status";
        }

        try {
            LocalDate.parse(dto.getTransactionDate());
        } catch (Exception e) {
            return "invalid transactionDate";
        }

        return null;
    }
}