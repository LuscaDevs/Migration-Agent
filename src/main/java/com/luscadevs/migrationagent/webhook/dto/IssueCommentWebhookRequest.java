package com.luscadevs.migrationagent.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueCommentWebhookRequest(
                GithubCommentDto comment,
                GithubRepositoryDto repository,
                InstallationDto installation) {
}