package com.luscadevs.migrationagent.github.domain;

public record MigrationCommand(
                GithubExecutionContext context,
                String targetVersion,
                String rawCommand) {
}