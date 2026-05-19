package com.luscadevs.migrationagent.orchestration.domain;

public record ProjectMetadata(
        BuildTool buildTool,
        String javaVersion,
        boolean springBootProject,
        boolean jaxRsProject) {
}