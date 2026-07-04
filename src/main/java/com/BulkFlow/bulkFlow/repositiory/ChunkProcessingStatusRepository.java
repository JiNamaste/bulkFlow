package com.BulkFlow.bulkFlow.repositiory;

import com.BulkFlow.bulkFlow.entity.ChunkProcessingStatus;
import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChunkProcessingStatusRepository extends JpaRepository<ChunkProcessingStatus, Long> {

    List<ChunkProcessingStatus> findByJobId(Long jobId);

    List<ChunkProcessingStatus> findByStatusAndRetryCountLessThan(ChunkStatus status, Integer retryCount);
}