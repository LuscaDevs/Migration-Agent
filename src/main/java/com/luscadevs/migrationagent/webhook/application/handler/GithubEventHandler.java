package com.luscadevs.migrationagent.webhook.application.handler;

import com.luscadevs.migrationagent.github.domain.GithubEventType;
import com.luscadevs.migrationagent.webhook.domain.WebhookEvent;

public interface GithubEventHandler {

    boolean supports(GithubEventType eventType);

    void handle(WebhookEvent<?> event);
}