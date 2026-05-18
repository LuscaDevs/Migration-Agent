package com.luscadevs.migrationagent.github.application.parser;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.luscadevs.migrationagent.github.domain.GithubExecutionContext;
import com.luscadevs.migrationagent.github.domain.MigrationCommand;

@Component
public class GithubCommandParser {

    private static final Pattern MIGRATION_PATTERN = Pattern.compile("^@migration-agent\\s+migrate\\s+(java\\d+)$",
            Pattern.CASE_INSENSITIVE);

    public Optional<MigrationCommand> parse(
            Long installationId,
            String commentBody,
            String repository,
            String requester) {

        Matcher matcher = MIGRATION_PATTERN.matcher(commentBody.trim());

        if (!matcher.matches()) {
            return Optional.empty();
        }

        String targetVersion = matcher.group(1).toLowerCase();

        return Optional.of(
                new MigrationCommand(
                        new GithubExecutionContext(
                                repository,
                                requester,
                                installationId),
                        targetVersion,
                        commentBody));
    }
}