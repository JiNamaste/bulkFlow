package com.BulkFlow.bulkFlow.controller;

import com.BulkFlow.bulkFlow.dto.FileUploadResponse;
import com.BulkFlow.bulkFlow.entity.UploadJob;
import com.BulkFlow.bulkFlow.service.IFileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final IFileUploadService fileUploadService;

    @PostMapping("/upload")
    public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType) throws Exception {
        return fileUploadService.uploadFile(file, fileType);
    }

    @GetMapping("/{jobId}/status")
    public UploadJob getJobStatus(@PathVariable Long jobId) {
        return fileUploadService.getJobStatus(jobId);
    }
}
