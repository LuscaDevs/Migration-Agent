package com.luscadevs.migrationagent.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import tools.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubWebhookPayload {
    JsonNode data;
}
