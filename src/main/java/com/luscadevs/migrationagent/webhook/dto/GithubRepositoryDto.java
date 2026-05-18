package com.luscadevs.migrationagent.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryDto(
        Long id,
        String name,
        @JsonProperty("full_name") String fullName) {
}