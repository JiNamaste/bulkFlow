package com.BulkFlow.bulkFlow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;

    private String transactionId;

    private String loanAccountNo;

    private String customerName;

    private Double amount;

    private String status;

    private LocalDate transactionDate;

    private LocalDateTime createdAt;
}
