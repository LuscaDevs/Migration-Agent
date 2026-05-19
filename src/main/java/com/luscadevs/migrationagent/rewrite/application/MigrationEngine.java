package com.luscadevs.migrationagent.rewrite.application;

import java.nio.file.Path;

import com.luscadevs.migrationagent.orchestration.domain.ProjectMetadata;

public interface MigrationEngine {

    void migrate(Path repoPath, ProjectMetadata metadata);
}