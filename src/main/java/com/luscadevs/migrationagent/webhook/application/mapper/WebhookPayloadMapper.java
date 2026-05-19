package com.luscadevs.migrationagent.webhook.application.mapper;

import org.springframework.stereotype.Component;

import com.luscadevs.migrationagent.github.domain.GithubEventType;
import com.luscadevs.migrationagent.webhook.dto.IssueCommentWebhookRequest;

import tools.jackson.databind.ObjectMapper;

@Component
public class WebhookPayloadMapper {

    private final ObjectMapper objectMapper;

    public WebhookPayloadMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Object map(String rawPayload, GithubEventType eventType) {
        try {
            return switch (eventType) {
                case ISSUE_COMMENT ->
                    objectMapper.readValue(rawPayload, IssueCommentWebhookRequest.class);

                default ->
                    throw new IllegalArgumentException(
                            "Unsupported event: " + eventType);
            };

        } catch (Exception ex) {
            throw new RuntimeException("Error mapping webhook payload", ex);
        }
    }
}