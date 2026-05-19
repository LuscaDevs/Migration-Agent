package com.luscadevs.migrationagent.rewrite.infrastructure;

import java.nio.file.Path;
import java.util.List;

import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Recipe;
import org.springframework.stereotype.Component;

import com.luscadevs.migrationagent.orchestration.domain.ProjectMetadata;
import com.luscadevs.migrationagent.rewrite.application.MigrationEngine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OpenRewriteMigrationEngine implements MigrationEngine {

    private final OpenRewriteEngineExecutor rewriteEngineExecutor;

    public OpenRewriteMigrationEngine(OpenRewriteEngineExecutor rewriteEngineExecutor) {
        this.rewriteEngineExecutor = rewriteEngineExecutor;
    }

    @Override
    public void migrate(Path repoPath, ProjectMetadata metadata) {

        try {
            log.info("Starting OpenRewrite migration");

            List<Recipe> recipes = resolveRecipes(metadata);

            ExecutionContext ctx = new InMemoryExecutionContext(Throwable::printStackTrace);

            rewriteEngineExecutor.execute(repoPath, recipes, ctx);

            log.info("OpenRewrite migration finished");

        } catch (Exception ex) {
            throw new RuntimeException("Rewrite execution failed", ex);
        }
    }

    private List<Recipe> resolveRecipes(ProjectMetadata metadata) {

        return List.of(
                new org.openrewrite.java.migrate.UpgradeJavaVersion(21));
    }
}