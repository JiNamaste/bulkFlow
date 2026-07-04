package com.BulkFlow.bulkFlow.handler;

import com.BulkFlow.bulkFlow.dto.TransactionRecordDto;
import com.BulkFlow.bulkFlow.entity.TransactionErrorRecord;
import com.BulkFlow.bulkFlow.entity.TransactionRecord;
import com.BulkFlow.bulkFlow.repositiory.TransactionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionBulkHandler implements BulkFileHandler<TransactionRecordDto, TransactionRecord, TransactionErrorRecord> {

    private final TransactionRecordRepository transactionRecordRepository;
    @Override
    public String fileType() {
        return "TRANSACTION";
    }

    @Override
    public TransactionRecordDto map(String line) {

        String[] data = line.split(",");

        return TransactionRecordDto.builder()
                .transactionId(data[0])
                .loanAccountNo(data[1])
                .customerName(data[2])
                .amount(Double.parseDouble(data[3]))
                .status(data[4])
                .transactionDate(data[5])
                .rawData(line)
                .build();
    }

    @Override
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

    @Override
    public TransactionRecord toEntity(TransactionRecordDto dto, Long jobId) {

        return TransactionRecord.builder()
                .jobId(jobId)
                .transactionId(dto.getTransactionId())
                .loanAccountNo(dto.getLoanAccountNo())
                .customerName(dto.getCustomerName())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .transactionDate(LocalDate.parse(dto.getTransactionDate()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public String mainTableName() {
        return "transaction_record";
    }

    @Override
    public String errorTableName() {
        return "transaction_record_error";
    }

    @Override
    public TransactionErrorRecord toErrorEntity(String rawData, String errorMessage, Long jobId) {
        return TransactionErrorRecord.builder()
                .jobId(jobId)
                .rawData(rawData)
                .errorReason(errorMessage)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public boolean alreadyExists(TransactionRecordDto dto, Long jobId) {
        return transactionRecordRepository.existsByJobIdAndTransactionId(jobId, dto.getTransactionId());
    }
}
