package com.BulkFlow.bulkFlow.scheduler;

import com.BulkFlow.bulkFlow.dto.ChunkResult;
import com.BulkFlow.bulkFlow.entity.ChunkProcessingStatus;
import com.BulkFlow.bulkFlow.entity.UploadJob;
import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;
import com.BulkFlow.bulkFlow.handler.BulkFileHandler;
import com.BulkFlow.bulkFlow.handler.BulkFileHandlerFactory;
import com.BulkFlow.bulkFlow.proccessor.ChunkProcessorService;
import com.BulkFlow.bulkFlow.repositiory.ChunkProcessingStatusRepository;
import com.BulkFlow.bulkFlow.repositiory.UploadJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ChunkRetryScheduler {

    private static final int MAX_RETRY_COUNT = 3;

    private final ChunkProcessingStatusRepository chunkRepository;
    private final UploadJobRepository uploadJobRepository;
    private final BulkFileHandlerFactory handlerFactory;
    private final ChunkProcessorService chunkProcessorService;

    @Scheduled(cron = "${scheduler.chunk-retry.cron}")
    public void retryFailedChunks() {

        List<ChunkProcessingStatus> failedChunks =
                chunkRepository.findByStatusAndRetryCountLessThan(
                        ChunkStatus.FAILED, MAX_RETRY_COUNT);

        for (ChunkProcessingStatus chunk : failedChunks) {

            try {
                UploadJob job = uploadJobRepository.findById(chunk.getJobId())
                        .orElseThrow(() -> new RuntimeException("Job not found"));

                BulkFileHandler<?, ?, ?> handler = handlerFactory.getHandler(job.getFileType());

                List<String> lines = readChunkLines(
                        job.getFilePath(),
                        chunk.getStartLine(),
                        chunk.getEndLine()
                );

                chunk.setRetryCount(chunk.getRetryCount() + 1);
                chunk.setUpdatedAt(LocalDateTime.now());
                chunkRepository.save(chunk);

                CompletableFuture<ChunkResult> future = chunkProcessorService.processChunk(
                        chunk.getId(), lines, handler, job.getId());
                future.join();
            } catch (Exception ex) {
                chunk.setErrorMessage(ex.getMessage());
                chunk.setUpdatedAt(LocalDateTime.now());
                if (chunk.getRetryCount() >= MAX_RETRY_COUNT) {
                    chunk.setStatus(ChunkStatus.PERMANENT_FAILED);
                } else {
                    chunk.setStatus(ChunkStatus.FAILED);
                }

                chunkRepository.save(chunk);
            }
        }
    }

    private List<String> readChunkLines(String filePath, Long startLine, Long endLine) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {

            reader.readLine();

            String line;
            long currentLine = 0;

            while ((line = reader.readLine()) != null) {

                currentLine++;

                if (currentLine >= startLine && currentLine <= endLine) {
                    lines.add(line);
                }

                if (currentLine > endLine) {
                    break;
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to read chunk lines: " + ex.getMessage());
        }

        return lines;
    }
}