package com.luscadevs.migrationagent.github.domain;

public enum GithubEventType {
    ISSUE_COMMENT,
    PUSH,
    PULL_REQUEST,
    INSTALLATION,
    UNKNOWN;

    public static GithubEventType fromHeader(String eventHeader) {
        try {
            return GithubEventType.valueOf(eventHeader.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}