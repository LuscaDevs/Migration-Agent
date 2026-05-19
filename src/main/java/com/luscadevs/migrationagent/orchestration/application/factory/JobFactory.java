package com.luscadevs.migrationagent.orchestration.application.factory;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.luscadevs.migrationagent.github.domain.MigrationCommand;
import com.luscadevs.migrationagent.orchestration.domain.JobStatus;
import com.luscadevs.migrationagent.orchestration.domain.JobType;
import com.luscadevs.migrationagent.orchestration.domain.MigrationJob;

@Component
public class JobFactory {

    public MigrationJob create(MigrationCommand command) {

        return new MigrationJob(
                UUID.randomUUID(),
                JobType.JAVA_MIGRATION,
                command.context().repository(),
                command.context().requester(),
                command.targetVersion(),
                command.context().installationId(),
                LocalDateTime.now(),
                JobStatus.CREATED);
    }
}