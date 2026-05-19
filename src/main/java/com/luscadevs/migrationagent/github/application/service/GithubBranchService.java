package com.luscadevs.migrationagent.github.application.service;

import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GithubBranchService {

    private static final Logger log = LoggerFactory.getLogger(GithubBranchService.class);

    public boolean createBranchCommitAndPush(
            Path repoPath,
            String repository,
            String token,
            String branchName,
            String commitMessage) {
        try (Git git = Git.open(repoPath.toFile())) {

            git.checkout()
                    .setCreateBranch(true)
                    .setName(branchName)
                    .call();

            Status status = git.status().call();
            if (status.isClean()) {
                log.info("No migration changes detected for repository {}", repository);
                return false;
            }

            git.add().addFilepattern(".").call();
            git.commit().setMessage(commitMessage).call();

            git.push()
                    .setRemote("origin")
                    .setRefSpecs(new RefSpec(branchName + ":" + branchName))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("x-access-token", token))
                    .call();

            log.info("Migration branch pushed: {}", branchName);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error creating migration branch", ex);
        }
    }
}
