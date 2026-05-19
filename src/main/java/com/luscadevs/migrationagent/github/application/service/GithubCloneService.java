package com.luscadevs.migrationagent.github.application.service;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GithubCloneService {

    public Path cloneRepository(
            String repository,
            String token) {

        try {

            Path tempDir = Files.createTempDirectory(
                    "migration-agent-");

            String cloneUrl = "https://x-access-token:" +
                    token +
                    "@github.com/" +
                    repository +
                    ".git";

            Git.cloneRepository()
                    .setURI(cloneUrl)
                    .setDirectory(tempDir.toFile())
                    .call();

            log.info(
                    "Repository cloned: {} -> {}",
                    repository,
                    tempDir);

            return tempDir;

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Error cloning repository",
                    ex);
        }
    }
}