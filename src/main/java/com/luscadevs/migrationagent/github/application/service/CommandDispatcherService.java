package com.luscadevs.migrationagent.github.application.service;

import org.springframework.stereotype.Service;

import com.luscadevs.migrationagent.github.domain.MigrationCommand;
import com.luscadevs.migrationagent.orchestration.application.service.JobDispatcherService;

@Service
public class CommandDispatcherService {

    private final JobDispatcherService jobDispatcherService;

    public CommandDispatcherService(JobDispatcherService jobDispatcherService) {
        this.jobDispatcherService = jobDispatcherService;
    }

    public void dispatch(MigrationCommand command) {
        jobDispatcherService.dispatch(command);
    }
}