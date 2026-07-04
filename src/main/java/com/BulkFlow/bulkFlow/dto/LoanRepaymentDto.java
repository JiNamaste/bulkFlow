package com.BulkFlow.bulkFlow.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRepaymentDto {

    private String repaymentId;
    private String loanAccountNo;
    private String customerId;
    private String customerName;
    private String mobileNumber;
    private String email;
    private String branchCode;
    private String ifscCode;
    private String paymentMode;
    private Double amount;
    private Double penaltyAmount;
    private Double totalAmount;
    private String status;
    private String paymentDate;
    private String dueDate;
    private String referenceNumber;
    private String remarks;
    private String rawData;
}