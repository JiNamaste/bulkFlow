package com.BulkFlow.bulkFlow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_type_config")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileTypeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fileType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String expectedHeaders;

    private Boolean active;
}