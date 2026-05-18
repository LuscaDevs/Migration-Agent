package com.luscadevs.migrationagent.github.domain;

public record GithubExecutionContext(
        String repository,
        String requester,
        Long installationId) {
}