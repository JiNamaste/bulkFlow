package com.BulkFlow.bulkFlow.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRecordDto {

    private String transactionId;
    private String loanAccountNo;
    private String customerName;
    private Double amount;
    private String status;
    private String transactionDate;
    private String rawData;
}
