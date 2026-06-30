package com.BulkFlow.bulkFlow.service;

import com.BulkFlow.bulkFlow.dto.FileUploadResponse;
import com.BulkFlow.bulkFlow.entity.UploadJob;
import com.BulkFlow.bulkFlow.repositiory.UploadJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileUploadService implements IFileUploadService{

    private final UploadJobRepository uploadJobRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileUploadResponse uploadFile(MultipartFile file){
        UploadJob savedJob = null;
    try {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new RuntimeException("Only CSV file is allowed");
        }

        Files.createDirectories(Path.of(uploadDir));

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Path.of(uploadDir, fileName);

        Files.copy(file.getInputStream(), filePath);

        UploadJob job = UploadJob.builder()
                .fileName(fileName)
                .filePath(filePath.toString())
                .status("UPLOADED")
                .totalRecords(0L)
                .successRecords(0L)
                .failedRecords(0L)
                .startedAt(LocalDateTime.now())
                .build();

         savedJob = uploadJobRepository.save(job);
    } catch (Exception ignored){

    }


        return FileUploadResponse.builder()
                .jobId(savedJob.getId())
                .fileName(savedJob.getFileName())
                .status(savedJob.getStatus())
                .message("File uploaded successfully.")
                .build();
    }

    @Override
    public UploadJob getJobStatus(Long jobId) {
        return uploadJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }


}
