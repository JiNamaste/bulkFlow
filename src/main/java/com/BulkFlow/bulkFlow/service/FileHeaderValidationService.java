package com.BulkFlow.bulkFlow.service;

import com.BulkFlow.bulkFlow.entity.FileTypeConfig;
import com.BulkFlow.bulkFlow.exception.FileValidationException;
import com.BulkFlow.bulkFlow.exception.UnsupportedFileTypeException;
import com.BulkFlow.bulkFlow.repositiory.FileTypeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileHeaderValidationService {

    private final FileTypeConfigRepository fileTypeConfigRepository;

    public void validateHeader(String fileType, String uploadedHeader) {

        FileTypeConfig config = fileTypeConfigRepository
                .findByFileTypeAndActiveTrue(fileType)
                .orElseThrow(() -> new RuntimeException("No active config found for fileType: " + fileType));

        String expectedHeader = normalize(config.getExpectedHeaders());
        String actualHeader = normalize(uploadedHeader);

        if (!expectedHeader.equals(actualHeader)) {
            throw new FileValidationException(
                    "Invalid file header for " + fileType +
                            ". Expected: " + config.getExpectedHeaders() + " but found: " + uploadedHeader
            );
        }
    }

    private String normalize(String header) {
        return header.replace(" ", "")
                .trim()
                .toLowerCase();
    }
}