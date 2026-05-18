package com.luscadevs.migrationagent.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubUserDto(
        Long id,
        String login) {
}