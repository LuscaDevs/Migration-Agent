package com.luscadevs.migrationagent.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubCommentDto(
        Long id,
        String body,
        GithubUserDto user) {
}