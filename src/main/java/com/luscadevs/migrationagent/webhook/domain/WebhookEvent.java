package com.luscadevs.migrationagent.webhook.domain;

import com.luscadevs.migrationagent.github.domain.GithubEventType;

public record WebhookEvent<T>(
                GithubEventType eventType,
                String signature,
                T payload) {
}