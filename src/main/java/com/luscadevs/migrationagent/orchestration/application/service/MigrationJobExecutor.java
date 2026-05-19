package com.luscadevs.migrationagent.orchestration.application.service;

import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.application.service.GithubBranchService;
import com.luscadevs.migrationagent.github.application.service.GithubCloneService;
import com.luscadevs.migrationagent.github.application.service.GithubInstallationTokenService;
import com.luscadevs.migrationagent.github.application.service.GithubPullRequestService;
import com.luscadevs.migrationagent.orchestration.domain.MigrationJob;
import com.luscadevs.migrationagent.orchestration.domain.ProjectMetadata;
import com.luscadevs.migrationagent.rewrite.infrastructure.OpenRewriteMigrationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MigrationJobExecutor {

    private static final Logger log = LoggerFactory.getLogger(MigrationJobExecutor.class);
        private final GithubInstallationTokenService githubInstallationTokenService;
        private final GithubCloneService githubCloneService;
        private final ProjectAnalyzerService projectAnalyzerService;
        private final OpenRewriteMigrationEngine migrationEngine;
        private final GithubBranchService githubBranchService;
        private final GithubPullRequestService githubPullRequestService;

        public MigrationJobExecutor(GithubInstallationTokenService githubInstallationTokenService,
                        GithubCloneService githubCloneService, ProjectAnalyzerService projectAnalyzerService,
                        OpenRewriteMigrationEngine migrationEngine, GithubBranchService githubBranchService,
                        GithubPullRequestService githubPullRequestService) {
                this.githubInstallationTokenService = githubInstallationTokenService;
                this.githubCloneService = githubCloneService;
                this.projectAnalyzerService = projectAnalyzerService;
                this.migrationEngine = migrationEngine;
                this.githubBranchService = githubBranchService;
                this.githubPullRequestService = githubPullRequestService;
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

                        String baseBranch = resolveBaseBranch(repoPath);
                        String branchName = buildBranchName(job);
                        boolean hasChanges = githubBranchService.createBranchCommitAndPush(
                                        repoPath,
                                        job.repository(),
                                        token,
                                        branchName,
                                        "Apply migration to " + job.targetVersion());

                        if (hasChanges) {
                                String prUrl = githubPullRequestService.createPullRequest(
                                                job.installationId(),
                                                job.repository(),
                                                branchName,
                                                baseBranch,
                                                "migration-agent: migrate to " + job.targetVersion(),
                                                "Automatic migration applied by Migration Agent.");

                                log.info("Pull request created: {}", prUrl);
                        } else {
                                log.info("No migration changes detected; skipping pull request creation.");
                        }

                        job.markCompleted();

                        log.info("Job completed: {}", job.id());
                } catch (Exception ex) {
                        job.markFailed();

                        log.error("Job failed: {}", job.id(), ex);
                }
        }

        private String buildBranchName(MigrationJob job) {
                return "migration-agent/" + job.targetVersion() + "-" + job.id().toString().substring(0, 8);
        }

        private String resolveBaseBranch(Path repoPath) {
                try (Git git = Git.open(repoPath.toFile())) {
                        return git.getRepository().getBranch();
                } catch (Exception ex) {
                        log.warn("Could not resolve base branch, defaulting to main", ex);
                        return "main";
                }
        }
}
