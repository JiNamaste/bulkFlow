package com.BulkFlow.bulkFlow.handler;

import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;

public interface BulkFileHandler<D, E,ER> {

    String fileType();

    D map(String line);

    String validate(D dto);

    E toEntity(D dto, Long jobId);
    String mainTableName();

    String errorTableName();
    ER toErrorEntity(String rawData, String errorMessage, Long jobId);

    boolean alreadyExists(D dto, Long jobId);
}