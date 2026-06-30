package com.BulkFlow.bulkFlow.service;

import com.BulkFlow.bulkFlow.dto.FileUploadResponse;
import com.BulkFlow.bulkFlow.entity.UploadJob;
import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {
    FileUploadResponse uploadFile(MultipartFile file);
    UploadJob getJobStatus(Long jobId);

}
