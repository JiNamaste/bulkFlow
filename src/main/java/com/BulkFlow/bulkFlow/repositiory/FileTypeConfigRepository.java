package com.BulkFlow.bulkFlow.repositiory;


import com.BulkFlow.bulkFlow.entity.FileTypeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileTypeConfigRepository extends JpaRepository<FileTypeConfig, Long> {

    Optional<FileTypeConfig> findByFileTypeAndActiveTrue(String fileType);
}