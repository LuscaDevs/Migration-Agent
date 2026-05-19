package com.luscadevs.migrationagent.orchestration.application.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.orchestration.domain.BuildTool;
import com.luscadevs.migrationagent.orchestration.domain.ProjectMetadata;

@Service
public class ProjectAnalyzerService {

    private static final Logger log = LoggerFactory.getLogger(ProjectAnalyzerService.class);

    public ProjectMetadata analyze(Path repoPath) {

        BuildTool buildTool = detectBuildTool(repoPath);

        String javaVersion = detectJavaVersion(repoPath);

        boolean springBoot = detectSpringBoot(repoPath);

        boolean jaxRs = detectJaxRs(repoPath);

        ProjectMetadata metadata = new ProjectMetadata(
                buildTool,
                javaVersion,
                springBoot,
                jaxRs);

        log.info(
                "Project analyzed: {}",
                metadata);

        return metadata;
    }

    private BuildTool detectBuildTool(Path path) {

        if (Files.exists(path.resolve("pom.xml"))) {
            return BuildTool.MAVEN;
        }

        if (Files.exists(path.resolve("build.gradle"))
                || Files.exists(path.resolve("build.gradle.kts"))) {
            return BuildTool.GRADLE;
        }

        return BuildTool.UNKNOWN;
    }

    private static final List<String> KNOWN_JAVA_VERSIONS = List.of("21", "17", "11", "8");

    private String detectJavaVersion(Path path) {

        try {

            Path pom = path.resolve("pom.xml");

            if (!Files.exists(pom)) {
                return "unknown";
            }

            String content = Files.readString(pom);
            String version = findJavaVersion(content);

            return switch (version) {
                case "21", "17", "11", "8" -> version;
                default -> "unknown";
            };

        } catch (Exception ex) {
            return "unknown";
        }
    }

    private String findJavaVersion(String content) {
        return KNOWN_JAVA_VERSIONS.stream()
                .filter(version -> content.contains("<java.version>" + version + "</java.version>"))
                .findFirst()
                .orElse("unknown");
    }

    private boolean detectJaxRs(Path path) {

        try {

            Path pom = path.resolve("pom.xml");

            if (!Files.exists(pom)) {
                return false;
            }

            String content = Files.readString(pom);

            return content.contains("javax.ws.rs")
                    || content.contains("jakarta.ws.rs");

        } catch (Exception ex) {
            return false;
        }
    }

    private boolean detectSpringBoot(Path path) {

        try {

            Path pom = path.resolve("pom.xml");

            if (!Files.exists(pom)) {
                return false;
            }

            String content = Files.readString(pom);

            return content.contains("spring-boot-starter")
                    || content.contains("org.springframework.boot");

        } catch (Exception ex) {
            return false;
        }
    }
}