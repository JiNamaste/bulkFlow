package com.BulkFlow.bulkFlow.proccessor;



import com.BulkFlow.bulkFlow.dto.ChunkResult;
import com.BulkFlow.bulkFlow.entity.ChunkProcessingStatus;
import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;
import com.BulkFlow.bulkFlow.handler.BulkFileHandler;
import com.BulkFlow.bulkFlow.repositiory.ChunkProcessingStatusRepository;
import com.BulkFlow.bulkFlow.service.GenericErrorRecordService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ChunkProcessorService {

    private final ChunkProcessingStatusRepository chunkRepository;
    private final EntityManager entityManager;
    private final ChunkStatusService chunkStatusService;

    @Async("chunkProcessorExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<ChunkResult> processChunk(Long chunkId, List<String> lines, BulkFileHandler handler, Long jobId) {

        ChunkProcessingStatus chunk = chunkRepository.findById(chunkId).orElseThrow(() -> new RuntimeException("Chunk not found"));

        long success = 0;
        long failed = 0;
        chunkStatusService.updateStatus(chunkId, ChunkStatus.PROCESSING, null);
        try {
            for (String line : lines) {
                try {
                    Object dto = handler.map(line);
                    String error = handler.validate(dto);

                    if (error == null) {
                        if (handler.alreadyExists(dto, jobId)) {
                            continue;
                        }
                        Object entity = handler.toEntity(dto, jobId);
                        entityManager.persist(entity);
                        success++;
                    } else {
                        Object errorEntity = handler.toErrorEntity(line, error, jobId);
                        entityManager.persist(errorEntity);
                        failed++;
                    }

                } catch (Exception ex) {
                    Object errorEntity = handler.toErrorEntity(line,ex.getMessage(), jobId);
                    entityManager.persist(errorEntity);
                    failed++;
                }
            }

            entityManager.flush();
            entityManager.clear();

            chunk.setStatus(ChunkStatus.SUCCESS);
            chunk.setUpdatedAt(LocalDateTime.now());
            chunkRepository.save(chunk);

            return CompletableFuture.completedFuture(
                    ChunkResult.builder()
                            .chunkId(chunkId)
                            .totalRecords((long) lines.size())
                            .successRecords(success)
                            .failedRecords(failed)
                            .build()
            );

        } catch (Exception ex) {
            chunk.setStatus(ChunkStatus.FAILED);
            chunk.setErrorMessage(ex.getMessage());
            chunk.setUpdatedAt(LocalDateTime.now());
            chunkRepository.save(chunk);

            return CompletableFuture.completedFuture(
                    ChunkResult.builder()
                            .chunkId(chunkId)
                            .totalRecords((long) lines.size())
                            .successRecords(success)
                            .failedRecords(lines.size() - success)
                            .build()
            );
        }
    }
}
