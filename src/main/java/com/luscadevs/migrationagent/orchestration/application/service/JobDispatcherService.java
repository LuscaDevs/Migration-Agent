package com.luscadevs.migrationagent.orchestration.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.domain.MigrationCommand;
import com.luscadevs.migrationagent.orchestration.application.factory.JobFactory;
import com.luscadevs.migrationagent.orchestration.domain.MigrationJob;

@Service
public class JobDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(JobDispatcherService.class);

    private final JobFactory jobFactory;
    private final MigrationJobExecutor jobExecutor;

    public JobDispatcherService(JobFactory jobFactory, MigrationJobExecutor jobExecutor) {
        this.jobFactory = jobFactory;
        this.jobExecutor = jobExecutor;
    }

    public void dispatch(MigrationCommand command) {

        MigrationJob job = jobFactory.create(command);

        log.info("Job created: {} for repository {}", job.id(), job.repository());

        jobExecutor.execute(job);
    }
}