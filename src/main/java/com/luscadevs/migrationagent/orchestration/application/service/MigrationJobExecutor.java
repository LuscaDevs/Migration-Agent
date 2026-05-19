package com.luscadevs.migrationagent.orchestration.application.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.application.service.GithubJwtService;
import com.luscadevs.migrationagent.orchestration.domain.MigrationJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MigrationJobExecutor {
    private final GithubJwtService githubJwtService;

    public MigrationJobExecutor(GithubJwtService githubJwtService) {
        this.githubJwtService = githubJwtService;
    }

    @Async
    public void execute(MigrationJob job) {

        try {
            job.markRunning();

            String jwt = githubJwtService.generateJwt();

            log.info(
                    "GitHub JWT generated: {}...",
                    jwt.substring(0, 30));

            log.info("Job started: {}", job.id());

            Thread.sleep(3000);

            job.markCompleted();

            log.info("Job completed: {}", job.id());
        } catch (Exception ex) {
            job.markFailed();

            log.error("Job failed: {}", job.id(), ex);
        }
    }
}