# BulkFlow

## Generic Fintech Bulk Processing Framework

BulkFlow is a high-performance bulk file processing framework designed for fintech and banking systems to process millions of records efficiently using asynchronous execution, chunk-based processing, multithreading, retry mechanisms, and generic handler architecture.

---

# Use Cases

## Banking
- EMI uploads
- Settlement reconciliation
- Loan booking imports

## Fintech
- Wallet transaction ingestion
- Merchant settlement uploads
- Payment reconciliation

## Insurance
- Bulk policy onboarding
- Claim settlement processing

---

# Design Patterns Used

- Strategy Pattern
- Factory Pattern
- Generic Framework Design
- Chunk Processing Pattern
- Retry Pattern

---

# Scalability Features

- Handles millions of records
- Supports multiple transaction types
- Parallel chunk processing
- Takes less processing time by splitting large files into concurrently processed chunks
- Retryable failed chunks
- Generic extensible architecture

---

# Features

- Generic bulk upload engine
- Dynamic file type support
- CSV upload processing
- Asynchronous background execution
- Parallel chunk processing
- Chunk-level retry mechanism
- Generic handler architecture using Strategy Pattern
- Header validation using DB configuration
- Duplicate protection / idempotency
- Generic error handling
- Job tracking
- Chunk tracking
- Batch persistence
- ControllerAdvice and custom exception handling
- Enterprise-grade scalable architecture

---

# Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- JdbcTemplate
- CompletableFuture
- ThreadPoolTaskExecutor
- Scheduler
- Maven

---

# Project Architecture

```text
                Upload API
                      |
               File Upload Service
                      |
                Save File Locally
                      |
                 Create Upload Job
                      |
             Async CsvProcessorService
                      |
              Read CSV in Chunks
                      |
          --------------------------------
          |              |              |
       Chunk 1        Chunk 2        Chunk 3
          |              |              |
   ChunkProcessor  ChunkProcessor  ChunkProcessor
          |              |              |
      BulkFileHandler (Strategy Pattern)
          |
  -----------------------------------------
  |                 |                    |
TransactionHandler CustomerHandler LoanHandler
          |
     Entity Persistence
          |
      PostgreSQL
```

---

# Core Components

## UploadJob

Tracks uploaded file processing.

Fields:
- fileName
- fileType
- status
- totalRecords
- successRecords
- failedRecords

---

## ChunkProcessingStatus

Tracks each chunk independently.

Fields:
- chunkNumber
- startLine
- endLine
- retryCount
- status
- errorMessage

---

## BulkFileHandler

Generic contract for all transaction types.

Responsibilities:
- CSV mapping
- Validation
- Entity conversion
- Error entity conversion
- Duplicate check

---

## CsvProcessorService

Responsibilities:
- Read CSV
- Create chunks
- Submit chunks in parallel
- Aggregate final result

---

## ChunkProcessorService

Responsibilities:
- Validate records
- Persist valid records
- Persist invalid records
- Update chunk status

---

# Supported File Types

Currently implemented:

- TRANSACTION
- LOAN_REPAYMENT

New file types can be added by creating:
- DTO
- Entity
- Error Entity
- Bulk Handler

without changing the core engine.

---

# Upload Flow

```text
User uploads CSV
        ↓
File validation
        ↓
Save file locally
        ↓
Create upload job
        ↓
Async background processing
        ↓
Read CSV in chunks
        ↓
Parallel chunk execution
        ↓
Validation + persistence
        ↓
Update job/chunk status
```

---

# Retry Mechanism

If a chunk fails:

```text
Chunk marked FAILED
        ↓
Scheduler picks failed chunks
        ↓
Retry only failed chunks
        ↓
Update retry count
        ↓
SUCCESS or PERMANENT_FAILED
```

---

# Header Validation

Expected CSV headers are configured dynamically in DB using:

```text
file_type_config
```

This avoids hardcoded column definitions.

---

# Error Handling

Invalid records are stored in dedicated error tables.

Examples:

```text
transaction_record_error
loan_repayment_record_error
```

---

# Performance Optimizations

- Async processing
- Parallel chunk execution
- Batch persistence
- Memory-safe streaming
- Chunk-level transactions
- Retry support
- Duplicate protection

---

# API Examples

## Upload File

```http
POST /api/files/upload?fileType=TRANSACTION
```

## Get Job Status

```http
GET /api/jobs/{jobId}/status
```

---

# How to Run the Project

## 1. Clone Repository

```bash
git clone <your-github-url>
cd bulkflow
```

---

## 2. Configure PostgreSQL

Create database:

```sql
CREATE DATABASE bulkflow_db;
```

Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bulkflow_db
    username: postgres
    password: root
```

---

## 3. Run Application

```bash
mvn clean install
mvn spring-boot:run
```

Application starts on:

```text
http://localhost:8080
```

---

## 4. Insert File Type Config

### TRANSACTION

```sql
INSERT INTO file_type_config (file_type, expected_headers, active)
VALUES (
    'TRANSACTION',
    'transactionId,loanAccountNo,customerName,amount,status,transactionDate',
    true
);
```

### LOAN_REPAYMENT

```sql
INSERT INTO file_type_config (file_type, expected_headers, active)
VALUES (
    'LOAN_REPAYMENT',
    'repaymentId,loanAccountNo,customerId,customerName,mobileNumber,email,branchCode,ifscCode,paymentMode,amount,penaltyAmount,totalAmount,status,paymentDate,dueDate,referenceNumber,remarks',
    true
);
```

---

## 5. Upload CSV File

### Upload Transaction File

```http
POST /api/files/upload?fileType=TRANSACTION
```

### Upload Loan Repayment File

```http
POST /api/files/upload?fileType=LOAN_REPAYMENT
```

Request Type:

```text
multipart/form-data
```

Key:

```text
file
```

---

## 6. Check Job Status

```http
GET /api/jobs/{jobId}/status
```

---

## 7. Retry Mechanism

Failed chunks are automatically retried by scheduler.

Retry interval:

```text
Every 5 minutes
```

---

# Future Enhancements

- Kafka integration
- Redis caching
- S3 storage
- WebSocket progress tracking
- Docker & Kubernetes deployment
- Prometheus & Grafana monitoring
- JMeter load testing

---
