package dynamicdudes.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicdudes.dto.ProjectRegistrationRequest;
import dynamicdudes.dto.ProjectResponse;
import dynamicdudes.dto.ProjectStatusRequest;
import dynamicdudes.model.Customer;
import dynamicdudes.model.ProjectTask;
import dynamicdudes.service.CustomerService;
import dynamicdudes.service.ProjectTaskService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;
    private final CustomerService customerService;

    public ProjectTaskController(
            ProjectTaskService projectTaskService,
            CustomerService customerService) {

        this.projectTaskService = projectTaskService;
        this.customerService = customerService;
    }

    @GetMapping("/admin/project-analytics")
    public Map<String, Object> getProjectAnalytics() {
        return projectTaskService.getProjectAnalytics();
    }

    @GetMapping("/admin/projects")
    public List<ProjectTask> getProjects() {
        return projectTaskService.getAllTasks();
    }

    @PutMapping("/admin/projects/{id}/status")
    public ProjectResponse updateProjectStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProjectStatusRequest request) {

        return ProjectResponse.from(
                projectTaskService.updateProjectStatus(id, request.status())
        );
    }

    @GetMapping("/customer/dashboard")
    public Map<String, Object> getCustomerDashboard(
            Authentication authentication) {

        Customer currentCustomer =
                currentCustomer(authentication);

        return projectTaskService.getCustomerDashboardData(
                currentCustomer.getId()
        );
    }

    @GetMapping("/customer/projects")
    public List<ProjectResponse> getCustomerProjects(
            Authentication authentication) {

        return projectTaskService.getCustomerProjects(
                currentCustomer(authentication)
        );
    }

    @GetMapping("/customer/projects/{id}")
    public ProjectResponse getCustomerProject(
            @PathVariable Long id,
            Authentication authentication) {

        return projectTaskService.getCustomerProject(
                id,
                currentCustomer(authentication)
        );
    }

    @PostMapping("/customer/projects")
    public ProjectResponse registerProject(
            Authentication authentication,
            @Valid @RequestBody ProjectRegistrationRequest request) {

        return projectTaskService.registerCustomerProject(
                currentCustomer(authentication),
                request
        );
    }

    private Customer currentCustomer(
            Authentication authentication) {

        return customerService.findByUsernameOrEmail(
                authentication.getName()
        );
    }
}
