package com.luscadevs.migrationagent.orchestration.application.service;

import java.nio.file.Path;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.application.service.GithubCloneService;
import com.luscadevs.migrationagent.github.application.service.GithubInstallationTokenService;
import com.luscadevs.migrationagent.orchestration.domain.MigrationJob;
import com.luscadevs.migrationagent.orchestration.domain.ProjectMetadata;
import com.luscadevs.migrationagent.rewrite.infrastructure.OpenRewriteMigrationEngine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MigrationJobExecutor {
    private final GithubInstallationTokenService githubInstallationTokenService;
    private final GithubCloneService githubCloneService;
    private final ProjectAnalyzerService projectAnalyzerService;
    private final OpenRewriteMigrationEngine migrationEngine;

    public MigrationJobExecutor(GithubInstallationTokenService githubInstallationTokenService,
            GithubCloneService githubCloneService, ProjectAnalyzerService projectAnalyzerService,
            OpenRewriteMigrationEngine migrationEngine) {
        this.githubInstallationTokenService = githubInstallationTokenService;
        this.githubCloneService = githubCloneService;
        this.projectAnalyzerService = projectAnalyzerService;
        this.migrationEngine = migrationEngine;
    }

    @Async
    public void execute(MigrationJob job) {

        try {
            job.markRunning();

            String token = githubInstallationTokenService
                    .generateInstallationToken(
                            job.installationId());

            log.info(
                    "Installation token generated: {}...",
                    token.substring(0, 20));

            Path repoPath = githubCloneService.cloneRepository(
                    job.repository(),
                    token);

            log.info(
                    "Repository available at {}",
                    repoPath);

            ProjectMetadata metadata = projectAnalyzerService.analyze(
                    repoPath);

            log.info(
                    "Detected project | buildTool={} | java={} | springBoot={} | jaxRs={}",
                    metadata.buildTool(),
                    metadata.javaVersion(),
                    metadata.springBootProject(),
                    metadata.jaxRsProject());

            migrationEngine.migrate(repoPath, metadata);
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