package com.BulkFlow.bulkFlow.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkResult {

    private Long chunkId;
    private long totalRecords;
    private long successRecords;
    private long failedRecords;
}