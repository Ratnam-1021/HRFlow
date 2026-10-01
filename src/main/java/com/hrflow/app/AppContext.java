package com.hrflow.app;

import com.hrflow.service.CandidateService;
import com.hrflow.service.SchedulingService;
import com.hrflow.storage.DatabaseManager;

/**
 * Service container strictly for Recruitment & Hiring Subsystem.
 */
public class AppContext {
    private static AppContext instance;

    private final CandidateService candidateService;
    private final SchedulingService schedulingService;

    private AppContext() {
        // Initialize SQLite and seed candidate dataset
        DatabaseManager.getInstance().seedInitialDataIfEmpty();

        this.candidateService = new CandidateService();
        this.schedulingService = new SchedulingService();

        // Seed default initial interview sessions for recruitment pipeline
        var topCandidates = candidateService.rankCandidates(java.util.Set.of("Java", "Spring Boot", "SQL"), 8);
        this.schedulingService.autoSchedule(topCandidates, java.time.LocalDate.now().plusDays(1));
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public CandidateService getCandidateService() { return candidateService; }
    public SchedulingService getSchedulingService() { return schedulingService; }
}
