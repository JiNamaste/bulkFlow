package com.BulkFlow.bulkFlow.repositiory;



import com.BulkFlow.bulkFlow.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
}
