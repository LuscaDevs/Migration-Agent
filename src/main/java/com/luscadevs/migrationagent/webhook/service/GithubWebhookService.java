package com.luscadevs.migrationagent.webhook.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

@Service
@Slf4j
public class GithubWebhookService {

    public void process(String event, String signature, JsonNode payload) {
        String repository = extractRepositoryName(payload);

        log.info("Webhook event received: {}", event);
        log.info("Repository: {}", repository);
        log.info("Received at: {}", LocalDateTime.now());
    }

    private String extractRepositoryName(JsonNode payload) {
        if (payload.has("repository") &&
                payload.get("repository").has("full_name")) {
            return payload.get("repository").get("full_name").asString();
        }

        return "unknown";
    }
}