package com.BulkFlow.bulkFlow.proccessor;

import com.BulkFlow.bulkFlow.entity.ChunkProcessingStatus;
import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;
import com.BulkFlow.bulkFlow.repositiory.ChunkProcessingStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChunkStatusService {

    private final ChunkProcessingStatusRepository chunkRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Long chunkId, ChunkStatus status, String errorMessage) {
        ChunkProcessingStatus chunk = chunkRepository.findById(chunkId)
                .orElseThrow(() -> new RuntimeException("Chunk not found"));

        chunk.setStatus(status);
        chunk.setErrorMessage(errorMessage);
        chunk.setUpdatedAt(LocalDateTime.now());
        chunkRepository.save(chunk);
    }
}
