package com.luscadevs.migrationagent.webhook.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.domain.GithubEventType;
import com.luscadevs.migrationagent.webhook.application.handler.GithubEventHandler;
import com.luscadevs.migrationagent.webhook.application.mapper.WebhookPayloadMapper;
import com.luscadevs.migrationagent.webhook.domain.WebhookEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessWebhookUseCase {

    private final List<GithubEventHandler> handlers;
    private final WebhookPayloadMapper mapper;

    public void execute(
            String eventHeader,
            String signature,
            String rawPayload) {

        log.info("Received GitHub event: {}", eventHeader);
        GithubEventType eventType = GithubEventType.fromHeader(eventHeader);

        if (GithubEventType.UNKNOWN.equals(eventType)) {
            return;
        }

        Object payload = mapper.map(rawPayload, eventType);

        WebhookEvent<Object> event = new WebhookEvent<>(eventType, signature, payload);

        GithubEventHandler handler = resolveHandler(eventType);

        handler.handle(event);
    }

    private GithubEventHandler resolveHandler(GithubEventType eventType) {

        return handlers.stream()
                .filter(handler -> handler.supports(eventType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No handler found for event: " + eventType));
    }
}