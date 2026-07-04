package com.BulkFlow.bulkFlow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "loan_repayment_record",
        uniqueConstraints = { @UniqueConstraint(
                        name = "uk_loan_repayment_record_job_repayment",
                        columnNames = {"job_id", "repayment_id"})}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRepaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;

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

    private LocalDate paymentDate;

    private LocalDate dueDate;

    private String referenceNumber;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private LocalDateTime createdAt;
}