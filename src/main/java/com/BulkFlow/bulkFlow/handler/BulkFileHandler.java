package com.BulkFlow.bulkFlow.handler;

import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;

public interface BulkFileHandler<D, E> {

    String fileType();

    D map(String line);

    String validate(D dto);

    E toEntity(D dto, Long jobId);
    String mainTableName();

    String errorTableName();

}