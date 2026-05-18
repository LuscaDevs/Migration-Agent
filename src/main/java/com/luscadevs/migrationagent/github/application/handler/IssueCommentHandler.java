package com.luscadevs.migrationagent.github.application.handler;

import org.springframework.stereotype.Component;

import com.luscadevs.migrationagent.github.application.parser.GithubCommandParser;
import com.luscadevs.migrationagent.github.application.service.CommandDispatcherService;
import com.luscadevs.migrationagent.github.domain.GithubEventType;
import com.luscadevs.migrationagent.webhook.application.handler.GithubEventHandler;
import com.luscadevs.migrationagent.webhook.domain.WebhookEvent;
import com.luscadevs.migrationagent.webhook.dto.IssueCommentWebhookRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueCommentHandler implements GithubEventHandler {

    private final GithubCommandParser commandParser;
    private final CommandDispatcherService dispatcherService;

    @Override
    public boolean supports(GithubEventType eventType) {
        return GithubEventType.ISSUE_COMMENT.equals(eventType);
    }

    @Override
    public void handle(WebhookEvent<?> event) {

        IssueCommentWebhookRequest payload = (IssueCommentWebhookRequest) event.payload();

        String repository = payload.repository().fullName();

        String requester = payload.comment().user().login();

        String commentBody = payload.comment().body();

        Long installationId = payload.installation().id();

        log.info(
                "Processing issue_comment | repo={} | requester={} | installationId={}",
                repository,
                requester,
                installationId);

        commandParser.parse(
                installationId,
                commentBody,
                repository,
                requester)
                .ifPresentOrElse(
                        dispatcherService::dispatch,
                        () -> log.info(
                                "Comment ignored: not a command"));
    }
}