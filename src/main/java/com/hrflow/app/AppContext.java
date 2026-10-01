package com.hrflow.app;

import com.hrflow.service.*;
import com.hrflow.storage.DatabaseManager;

/**
 * Global Dependency Injection and Service Container.
 */
public class AppContext {
    private static AppContext instance;

    private final CandidateService candidateService;
    private final EmployeeService employeeService;
    private final LeaveService leaveService;
    private final SchedulingService schedulingService;
    private final TaskService taskService;
    private final AutomationCenterService automationCenterService;

    private AppContext() {
        // Initialize SQLite and seed sample data
        DatabaseManager.getInstance().seedInitialDataIfEmpty();

        this.candidateService = new CandidateService();
        this.employeeService = new EmployeeService();
        this.leaveService = new LeaveService();
        this.schedulingService = new SchedulingService();
        this.taskService = new TaskService();
        this.automationCenterService = new AutomationCenterService(
                candidateService, employeeService, leaveService, schedulingService, taskService
        );
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public CandidateService getCandidateService() { return candidateService; }
    public EmployeeService getEmployeeService() { return employeeService; }
    public LeaveService getLeaveService() { return leaveService; }
    public SchedulingService getSchedulingService() { return schedulingService; }
    public TaskService getTaskService() { return taskService; }
    public AutomationCenterService getAutomationCenterService() { return automationCenterService; }
}
