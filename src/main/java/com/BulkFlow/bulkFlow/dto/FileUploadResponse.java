package com.BulkFlow.bulkFlow.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadResponse {

    private Long jobId;
    private String fileName;
    private String status;
    private String message;
}
