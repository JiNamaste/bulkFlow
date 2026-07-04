package com.BulkFlow.bulkFlow.handler;


import com.BulkFlow.bulkFlow.exception.UnsupportedFileTypeException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BulkFileHandlerFactory {

    private final Map<String, BulkFileHandler<?, ?,?>> handlerMap;

    public BulkFileHandlerFactory(List<BulkFileHandler<?, ?,?>> handlers) {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        handler -> handler.fileType().toUpperCase(),
                        handler -> handler
                ));
    }

    public BulkFileHandler<?, ?,?> getHandler(String fileType) {

        BulkFileHandler<?, ?,?> handler = handlerMap.get(fileType.toUpperCase());

        if (handler == null) {
            throw new UnsupportedFileTypeException("No handler found for fileType: " + fileType);
        }

        return handler;
    }
}