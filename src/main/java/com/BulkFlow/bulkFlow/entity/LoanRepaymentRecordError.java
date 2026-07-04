package com.BulkFlow.bulkFlow.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayment_record_error")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRepaymentRecordError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;

    @Column(columnDefinition = "TEXT")
    private String rawData;

    @Column(columnDefinition = "TEXT")
    private String errorReason;

    private LocalDateTime createdAt;
}