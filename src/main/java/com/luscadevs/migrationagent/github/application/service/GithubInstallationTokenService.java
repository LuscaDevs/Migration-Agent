package com.luscadevs.migrationagent.github.application.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.luscadevs.migrationagent.github.dto.InstallationTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubInstallationTokenService {

    private final GithubJwtService githubJwtService;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.github.com")
            .build();

    public String generateInstallationToken(
            Long installationId) {

        String jwt = githubJwtService.generateJwt();

        InstallationTokenResponse response = restClient.post()
                .uri("/app/installations/{id}/access_tokens",
                        installationId)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + jwt)
                .header(
                        HttpHeaders.ACCEPT,
                        "application/vnd.github+json")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(InstallationTokenResponse.class);

        return response.token();
    }
}