package com.BulkFlow.bulkFlow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericErrorRecordService {

    private final JdbcTemplate jdbcTemplate;

    public void saveError(String tableName, Long jobId, String rawData, String errorReason) {

        String sql = """
                INSERT INTO %s (job_id, raw_data, error_reason, created_at)
                VALUES (?, ?, ?, NOW())
                """.formatted(tableName);

        jdbcTemplate.update(sql, jobId, rawData, errorReason);
    }
}
