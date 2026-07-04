package com.BulkFlow.bulkFlow.proccessor;

import com.BulkFlow.bulkFlow.dto.ChunkResult;
import com.BulkFlow.bulkFlow.entity.ChunkProcessingStatus;
import com.BulkFlow.bulkFlow.entity.UploadJob;
import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;
import com.BulkFlow.bulkFlow.handler.BulkFileHandler;
import com.BulkFlow.bulkFlow.handler.BulkFileHandlerFactory;
import com.BulkFlow.bulkFlow.repositiory.ChunkProcessingStatusRepository;
import com.BulkFlow.bulkFlow.repositiory.UploadJobRepository;
import com.BulkFlow.bulkFlow.service.FileHeaderValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CsvProcessorService {

    private static final int CHUNK_SIZE = 5000;

    private final UploadJobRepository uploadJobRepository;
    private final BulkFileHandlerFactory handlerFactory;
    private final ChunkProcessingStatusRepository chunkRepository;
    private final ChunkProcessorService chunkProcessorService;
    private final FileHeaderValidationService fileHeaderValidationService;

    @Async("fileProcessorExecutor")
    public void processFile(Long jobId) {

    UploadJob job = uploadJobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

        try {
            job.setStatus("PROCESSING");
            uploadJobRepository.save(job);

            BulkFileHandler handler = handlerFactory.getHandler(job.getFileType());

            List<CompletableFuture<ChunkResult>> futures = new ArrayList<>();

            long currentLine = 0;
            int chunkNumber = 1;

            try (BufferedReader reader = Files.newBufferedReader(Path.of(job.getFilePath()))) {

                String uploadedHeader = reader.readLine();
                fileHeaderValidationService.validateHeader(job.getFileType(), uploadedHeader);
                String line;
                List<String> chunkLines = new ArrayList<>();

                long chunkStartLine = 1;

                while ((line = reader.readLine()) != null) {

                    currentLine++;
                    chunkLines.add(line);

                    if (chunkLines.size() == CHUNK_SIZE) {

                 CompletableFuture<ChunkResult> future = submitChunk(job, handler,new ArrayList<>(chunkLines),
                                        chunkNumber, chunkStartLine, currentLine);

                        futures.add(future);

                        chunkLines.clear();
                        chunkNumber++;
                        chunkStartLine = currentLine + 1;
                    }
                }

                if (!chunkLines.isEmpty()) {
                    CompletableFuture<ChunkResult> future =
                            submitChunk(job, handler, new ArrayList<>(chunkLines),
                                    chunkNumber, chunkStartLine, currentLine);
                    futures.add(future);
                }
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            long totalRecords = 0;
            long successRecords = 0;
            long failedRecords = 0;

            for (CompletableFuture<ChunkResult> future : futures) {
                ChunkResult result = future.join();
                totalRecords += result.getTotalRecords();
                successRecords += result.getSuccessRecords();
                failedRecords += result.getFailedRecords();
            }

            boolean anyChunkFailed = chunkRepository.findByJobId(jobId)
                    .stream()
                    .anyMatch(chunk -> chunk.getStatus() == ChunkStatus.FAILED);

            job.setTotalRecords(totalRecords);
            job.setSuccessRecords(successRecords);
            job.setFailedRecords(failedRecords);
            job.setStatus(anyChunkFailed ? "PARTIALLY_COMPLETED" : "COMPLETED");
            job.setCompletedAt(LocalDateTime.now());

            uploadJobRepository.save(job);

        } catch (Exception ex) {

            job.setStatus("FAILED");
            job.setErrorMessage(ex.getMessage());
            job.setCompletedAt(LocalDateTime.now());
            uploadJobRepository.save(job);
        }
    }

    private CompletableFuture<ChunkResult> submitChunk(UploadJob job, BulkFileHandler handler, List<String> lines,
            int chunkNumber, long startLine, long endLine) {
        ChunkProcessingStatus chunkStatus = ChunkProcessingStatus.builder()
                .jobId(job.getId())
                .fileType(job.getFileType())
                .chunkNumber(chunkNumber)
                .startLine(startLine)
                .endLine(endLine)
                .status(ChunkStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ChunkProcessingStatus savedChunk = chunkRepository.save(chunkStatus);

        return chunkProcessorService.processChunk(savedChunk.getId(), lines, handler, job.getId());
    }
}