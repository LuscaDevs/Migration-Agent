package com.luscadevs.migrationagent.webhook.application;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.domain.GithubEventType;
import com.luscadevs.migrationagent.webhook.application.handler.GithubEventHandler;
import com.luscadevs.migrationagent.webhook.application.mapper.WebhookPayloadMapper;
import com.luscadevs.migrationagent.webhook.domain.WebhookEvent;

@Service
public class ProcessWebhookUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessWebhookUseCase.class);

    private final List<GithubEventHandler> handlers;
    private final WebhookPayloadMapper mapper;

    public ProcessWebhookUseCase(List<GithubEventHandler> handlers, WebhookPayloadMapper mapper) {
        this.handlers = handlers;
        this.mapper = mapper;
    }

    public void execute(
            String eventHeader,
            String signature,
            String rawPayload) {

        log.info("Received GitHub event: {}", eventHeader);
        GithubEventType eventType = GithubEventType.fromHeader(eventHeader);

        if (GithubEventType.UNKNOWN.equals(eventType)) {
            return;
        }

        GithubEventHandler handler = resolveHandler(eventType);
        if (handler == null) {
            log.info("No handler configured for event: {}. Ignoring.", eventType);
            return;
        }

        Object payload = mapper.map(rawPayload, eventType);

        WebhookEvent<Object> event = new WebhookEvent<>(eventType, signature, payload);

        handler.handle(event);
    }

    private GithubEventHandler resolveHandler(GithubEventType eventType) {

        return handlers.stream()
                .filter(handler -> handler.supports(eventType))
                .findFirst()
                .orElse(null);
    }
}