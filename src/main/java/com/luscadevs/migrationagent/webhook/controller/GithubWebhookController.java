package com.luscadevs.migrationagent.webhook.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luscadevs.migrationagent.webhook.application.ProcessWebhookUseCase;

@RestController
@RequestMapping("/api/github")
public class GithubWebhookController {

        private final ProcessWebhookUseCase processWebhookUseCase;

        public GithubWebhookController(ProcessWebhookUseCase processWebhookUseCase) {
                this.processWebhookUseCase = processWebhookUseCase;
        }

        @PostMapping("/webhook")
        public ResponseEntity<Map<String, String>> receiveWebhook(
                        @RequestHeader("X-GitHub-Event") String event,
                        @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
                        @RequestBody String payload) {

                processWebhookUseCase.execute(
                                event,
                                signature,
                                payload);

                return ResponseEntity.ok(
                                Map.of("status", "received"));
        }
}