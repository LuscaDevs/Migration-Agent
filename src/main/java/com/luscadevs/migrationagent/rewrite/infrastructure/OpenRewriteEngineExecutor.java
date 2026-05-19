package com.luscadevs.migrationagent.rewrite.infrastructure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.RecipeRun;
import org.openrewrite.Result;
import org.openrewrite.SourceFile;
import org.openrewrite.internal.InMemoryLargeSourceSet;
import org.openrewrite.java.JavaParser;
import org.openrewrite.maven.MavenParser;
import org.springframework.stereotype.Component;

@Component
public class OpenRewriteEngineExecutor {

    public void execute(Path repoPath, List<Recipe> recipes, ExecutionContext ctx) {

        try {
            JavaParser javaParser = JavaParser.fromJavaVersion()
                    .build();
            MavenParser mavenParser = MavenParser.builder()
                    .skipDependencyResolution(true)
                    .build();

            List<SourceFile> javaSourceFiles = javaParser.parse(
                    List.of(repoPath),
                    repoPath,
                    ctx).toList();

            List<Path> pomPaths = Files.walk(repoPath)
                    .filter(p -> p.getFileName().toString().equals("pom.xml"))
                    .collect(Collectors.toList());

            List<SourceFile> mavenSourceFiles = pomPaths.isEmpty() ? List.of()
                    : mavenParser.parse(pomPaths, repoPath, ctx).collect(Collectors.toList());

            List<SourceFile> sourceFiles = Stream.concat(javaSourceFiles.stream(), mavenSourceFiles.stream())
                    .collect(Collectors.toList());

            if (recipes.isEmpty()) {
                return;
            }

            Recipe recipe = recipes.get(0);
            InMemoryLargeSourceSet sourceSet = new InMemoryLargeSourceSet(sourceFiles);
            RecipeRun run = recipe.run(sourceSet, ctx);

            var results = run.getChangeset().getAllResults();
            if (results.isEmpty()) {
                System.out.println("No OpenRewrite changes generated.");
            }

            for (Result result : results) {
                Path afterPath = result.getAfter().getSourcePath();
                Path targetPath = afterPath.isAbsolute()
                        ? afterPath
                        : repoPath.resolve(afterPath).normalize();

                System.out.println("Changed: " + targetPath);
                Files.createDirectories(targetPath.getParent());
                Files.writeString(targetPath, result.getAfter().printAll(), result.getAfter().getCharset());
            }

        } catch (Exception e) {
            throw new RuntimeException("OpenRewrite execution failed", e);
        }
    }
}