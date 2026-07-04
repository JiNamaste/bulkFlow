package com.BulkFlow.bulkFlow.repositiory;

import com.BulkFlow.bulkFlow.entity.LoanRepaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepaymentRecordRepository extends JpaRepository<LoanRepaymentRecord,Long> {
    boolean existsByJobIdAndRepaymentId(Long jobId, String repaymentId);
}
