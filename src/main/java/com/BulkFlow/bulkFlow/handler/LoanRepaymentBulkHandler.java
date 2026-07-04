package com.BulkFlow.bulkFlow.handler;

import com.BulkFlow.bulkFlow.dto.LoanRepaymentDto;
import com.BulkFlow.bulkFlow.entity.LoanRepaymentRecord;
import com.BulkFlow.bulkFlow.entity.LoanRepaymentRecordError;
import com.BulkFlow.bulkFlow.repositiory.LoanRepaymentRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanRepaymentBulkHandler implements BulkFileHandler<LoanRepaymentDto,LoanRepaymentRecord,LoanRepaymentRecordError> {

    private final LoanRepaymentRecordRepository loanRepaymentRecordRepository;

    @Override
    public String fileType() {
        return "LOAN_REPAYMENT";
    }

    @Override
    public LoanRepaymentDto map(String line) {

        String[] data = line.split(",");

        return LoanRepaymentDto.builder()
                .repaymentId(data[0])
                .loanAccountNo(data[1])
                .customerId(data[2])
                .customerName(data[3])
                .mobileNumber(data[4])
                .email(data[5])
                .branchCode(data[6])
                .ifscCode(data[7])
                .paymentMode(data[8])
                .amount(Double.parseDouble(data[9]))
                .penaltyAmount(Double.parseDouble(data[10]))
                .totalAmount(Double.parseDouble(data[11]))
                .status(data[12])
                .paymentDate(data[13])
                .dueDate(data[14])
                .referenceNumber(data[15])
                .remarks(data[16])
                .rawData(line)
                .build();
    }

    @Override
    public String validate(LoanRepaymentDto dto) {

        if (dto.getRepaymentId() == null || dto.getRepaymentId().isBlank()) {
            return "repaymentId is required";
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

        if (dto.getTotalAmount() == null || dto.getTotalAmount() <= 0) {
            return "totalAmount should be greater than 0";
        }

        if (!List.of("SUCCESS", "FAILED", "PENDING")
                .contains(dto.getStatus())) {
            return "invalid status";
        }

        try {
            LocalDate.parse(dto.getPaymentDate());
            LocalDate.parse(dto.getDueDate());
        } catch (Exception ex) {
            return "invalid date format";
        }

        return null;
    }

    @Override
    public LoanRepaymentRecord toEntity(LoanRepaymentDto dto, Long jobId) {

        return LoanRepaymentRecord.builder()
                .jobId(jobId)
                .repaymentId(dto.getRepaymentId())
                .loanAccountNo(dto.getLoanAccountNo())
                .customerId(dto.getCustomerId())
                .customerName(dto.getCustomerName())
                .mobileNumber(dto.getMobileNumber())
                .email(dto.getEmail())
                .branchCode(dto.getBranchCode())
                .ifscCode(dto.getIfscCode())
                .paymentMode(dto.getPaymentMode())
                .amount(dto.getAmount())
                .penaltyAmount(dto.getPenaltyAmount())
                .totalAmount(dto.getTotalAmount())
                .status(dto.getStatus())
                .paymentDate(LocalDate.parse(dto.getPaymentDate()))
                .dueDate(LocalDate.parse(dto.getDueDate()))
                .referenceNumber(dto.getReferenceNumber())
                .remarks(dto.getRemarks())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public String mainTableName() {
        return "loan_repayment_record";
    }

    @Override
    public String errorTableName() {
        return "loan_repayment_record_error";
    }

    @Override
    public LoanRepaymentRecordError toErrorEntity(String rawData, String errorMessage, Long jobId) {

        return LoanRepaymentRecordError.builder()
                .jobId(jobId)
                .rawData(rawData)
                .errorReason(errorMessage)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public boolean alreadyExists(LoanRepaymentDto dto, Long jobId) {
        return loanRepaymentRecordRepository.existsByJobIdAndRepaymentId(jobId, dto.getRepaymentId());
    }
}