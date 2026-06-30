package com.BulkFlow.bulkFlow.repositiory;

import com.BulkFlow.bulkFlow.entity.UploadJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadJobRepository extends JpaRepository<UploadJob, Long> {
}
