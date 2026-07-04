package com.BulkFlow.bulkFlow.handler;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BulkFileHandlerFactory {

    private final Map<String, BulkFileHandler<?, ?>> handlerMap;

    public BulkFileHandlerFactory(List<BulkFileHandler<?, ?>> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        handler -> handler.fileType().toUpperCase(),
                        handler -> handler
                ));
    }

    public BulkFileHandler<?, ?> getHandler(String fileType) {

        BulkFileHandler<?, ?> handler = handlerMap.get(fileType.toUpperCase());

        if (handler == null) {
            throw new RuntimeException("No handler found for fileType: " + fileType);
        }

        return handler;
    }
}