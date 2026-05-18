package com.luscadevs.migrationagent.orchestration.application.service;

import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.domain.MigrationCommand;
import com.luscadevs.migrationagent.orchestration.application.factory.JobFactory;
import com.luscadevs.migrationagent.orchestration.domain.MigrationJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDispatcherService {

    private final JobFactory jobFactory;
    private final MigrationJobExecutor jobExecutor;

    public void dispatch(MigrationCommand command) {

        MigrationJob job = jobFactory.create(command);

        log.info("Job created: {} for repository {}", job.id(), job.repository());

        jobExecutor.execute(job);
    }
}