package com.luscadevs.migrationagent.orchestration.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class MigrationJob {

    private final UUID id;
    private final JobType type;
    private final String repository;
    private final String requester;
    private final String targetVersion;
    private final LocalDateTime createdAt;

    private JobStatus status;

    public MigrationJob(
            UUID id,
            JobType type,
            String repository,
            String requester,
            String targetVersion,
            LocalDateTime createdAt,
            JobStatus status) {
        this.id = id;
        this.type = type;
        this.repository = repository;
        this.requester = requester;
        this.targetVersion = targetVersion;
        this.createdAt = createdAt;
        this.status = status;
    }

    public UUID id() {
        return id;
    }

    public JobType type() {
        return type;
    }

    public String repository() {
        return repository;
    }

    public String requester() {
        return requester;
    }

    public String targetVersion() {
        return targetVersion;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public JobStatus status() {
        return status;
    }

    public void markRunning() {
        this.status = JobStatus.RUNNING;
    }

    public void markCompleted() {
        this.status = JobStatus.COMPLETED;
    }

    public void markFailed() {
        this.status = JobStatus.FAILED;
    }
}