package com.luscadevs.migrationagent.github.application.service;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.luscadevs.migrationagent.github.dto.GithubPullRequestResponse;

@Service
public class GithubPullRequestService {

    private static final String GITHUB_API_BASE = "https://api.github.com";

    private final GithubInstallationTokenService githubInstallationTokenService;
    private final RestClient restClient = RestClient.builder()
            .baseUrl(GITHUB_API_BASE)
            .build();

    public GithubPullRequestService(GithubInstallationTokenService githubInstallationTokenService) {
        this.githubInstallationTokenService = githubInstallationTokenService;
    }

    public String createPullRequest(
            Long installationId,
            String repository,
            String headBranch,
            String baseBranch,
            String title,
            String body) {

        String token = githubInstallationTokenService.generateInstallationToken(installationId);

        GithubPullRequestResponse response = restClient.post()
                .uri("/repos/" + repository + "/pulls")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "title", title,
                        "head", headBranch,
                        "base", baseBranch,
                        "body", body))
                .retrieve()
                .body(GithubPullRequestResponse.class);

        return response.htmlUrl();
    }
}
