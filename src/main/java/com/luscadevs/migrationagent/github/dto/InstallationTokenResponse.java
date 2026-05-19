package com.luscadevs.migrationagent.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstallationTokenResponse(
        String token) {
}