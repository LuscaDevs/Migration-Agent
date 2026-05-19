package com.luscadevs.migrationagent.webhook.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;

@Service
public class GithubWebhookService {

    private static final Logger log = LoggerFactory.getLogger(GithubWebhookService.class);

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