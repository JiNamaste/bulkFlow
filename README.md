# BulkFlow — Fintech Bulk Processing Framework

## Overview

BulkFlow is a high-performance fintech-oriented bulk file processing framework designed to handle millions of transaction records efficiently using asynchronous processing, multithreading, chunk-based execution, generic handlers, and batch persistence.

The framework is built to solve enterprise-scale financial data ingestion problems such as:

- Loan repayment uploads
- Settlement processing
- Payment reconciliation
- EMI transaction ingestion
- Customer onboarding bulk uploads
- Daily EOD processing
- Banking transaction imports

BulkFlow is designed as a reusable framework where any new transaction type can integrate by simply adding:

- DTO
- Entity
- Custom Handler

without modifying the core processing engine.

---

# Problem Statement

Financial systems process huge files daily containing lakhs or millions of records.

Traditional approaches face issues such as:

- API timeout
- Memory overflow
- Sequential processing bottlenecks
- DB overload
- Partial upload failures
- Duplicate records
- Retry complexity
- Tight coupling between file types and processing logic

BulkFlow solves these problems through:

- Asynchronous processing
- Chunk-based parallel execution
- Generic handler architecture
- Chunk-level tracking
- Failure recovery support
- Dynamic transaction-type handling

---

# Key Features

## Generic Bulk Upload Engine

Supports multiple file types dynamically:

```text
TRANSACTION
CUSTOMER
LOAN
PAYMENT
SETTLEMENT
```

---

## Asynchronous File Processing

Upload API immediately returns response while processing continues in background.

```text
Upload Request
      ↓
Create Job
      ↓
Return jobId immediately
      ↓
Background processing starts
```

---

## Chunk-Based Processing

Large CSV files are divided into smaller chunks.

Example:

```text
Chunk Size = 5000 records
```

Benefits:
- Memory optimization
- Better throughput
- Easier failure recovery

---

## Parallel Chunk Execution

Chunks are processed using thread pools.

```text
Thread 1 → Chunk 1
Thread 2 → Chunk 2
Thread 3 → Chunk 3
```

Benefits:
- Faster processing
- High CPU utilization
- Horizontal scalability

---

## Generic Handler Architecture

Each file type implements custom business logic using Strategy Pattern.

Example:

```text
TransactionBulkHandler
CustomerBulkHandler
LoanBulkHandler
```

Handler responsibilities:
- CSV mapping
- Validation
- Entity conversion
- Error table mapping

---

## Generic Error Handling

Invalid records are stored in dedicated error tables.

Example:

```text
transaction_record_error
customer_record_error
```

Error tables store:
- Raw record data
- Validation failure reason
- Processing timestamp

---

## Chunk-Level Tracking

Each chunk maintains processing status independently.

Statuses:

```text
PENDING
PROCESSING
SUCCESS
FAILED
PERMANENT_FAILED
```

Benefits:
- Retry failed chunks only
- Partial success support
- Better operational visibility

---

## Job Tracking

Upload jobs maintain:
- Total records
- Success count
- Failed count
- Processing status
- Error details

---

# System Architecture

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

Tracks file-level processing information.

Fields:
- fileName
- fileType
- status
- totalRecords
- successRecords
- failedRecords

---

## ChunkProcessingStatus

Tracks chunk-level execution.

Fields:
- chunkNumber
- startLine
- endLine
- retryCount
- status
- errorMessage

---

## BulkFileHandler

Generic contract implemented by all transaction types.

Responsibilities:
- CSV mapping
- Validation
- Entity transformation
- Error table resolution

---

## CsvProcessorService

Responsible for:
- Reading files
- Creating chunks
- Submitting chunks in parallel
- Aggregating chunk results

---

## ChunkProcessorService

Responsible for:
- Chunk-level validation
- Batch persistence
- Error handling
- Chunk status updates

---

# Technologies Used

```text
Java 21
Spring Boot
Spring Data JPA
JdbcTemplate
PostgreSQL
ThreadPoolTaskExecutor
CompletableFuture
Asynchronous Processing
Chunk Processing
Docker
Swagger
JMeter
```

---

# Performance Optimizations

## Asynchronous Processing
Avoids blocking upload API requests.

---

## Parallel Chunk Processing
Improves throughput for large datasets.

---

## Batch Persistence
Reduces DB round-trips.

---

## Chunk-Level Transactions
Ensures partial success handling.

---

## Memory-Safe Processing
Avoids loading full file into memory.

---

## Generic Framework Design
Supports multiple file types without changing core engine.

---

# Future Enhancements

## Retry Scheduler
Automatically retries failed chunks.

---

## Kafka Integration
Decouple ingestion from processing.

---

## Redis Integration
- Job progress caching
- Duplicate detection
- Rate limiting

---

## S3 / Cloud Storage
Store uploaded files externally.

---

## Monitoring
Prometheus + Grafana dashboards.

---

## Kubernetes Deployment
Horizontal scaling for chunk workers.

---

# Example Use Cases

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
