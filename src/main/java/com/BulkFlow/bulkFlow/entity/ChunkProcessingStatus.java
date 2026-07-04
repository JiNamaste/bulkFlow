package com.BulkFlow.bulkFlow.entity;

import com.BulkFlow.bulkFlow.enumpackage.ChunkStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkProcessingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;

    private String fileType;

    private Integer chunkNumber;

    private Long startLine;

    private Long endLine;

    @Enumerated(EnumType.STRING)
    private ChunkStatus status;

    private Integer retryCount;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
