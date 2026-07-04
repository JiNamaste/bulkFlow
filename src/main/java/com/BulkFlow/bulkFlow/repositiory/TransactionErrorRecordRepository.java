package com.BulkFlow.bulkFlow.repositiory;

import com.BulkFlow.bulkFlow.entity.TransactionErrorRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionErrorRecordRepository extends JpaRepository<TransactionErrorRecord, Long> {
}