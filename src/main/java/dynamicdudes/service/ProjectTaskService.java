package dynamicdudes.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import dynamicdudes.dto.ProjectRegistrationRequest;
import dynamicdudes.dto.ProjectResponse;
import dynamicdudes.model.Customer;
import dynamicdudes.model.ProjectTask;
import dynamicdudes.repository.CustomerRepository;
import dynamicdudes.repository.ProjectTaskRepository;

@Service
public class ProjectTaskService {

    private static final List<String> PROJECT_CATEGORIES = Arrays.asList(
            "Poster Making",
            "Photo Editing",
            "Video Editing",
            "AI Ad Video Creation",
            "Social Media Management",
            "SEO",
            "Website Designing",
            "App Designing",
            "Promo Videos"
    );

    private final ProjectTaskRepository projectTaskRepository;
    private final CustomerRepository customerRepository;

    public ProjectTaskService(
            ProjectTaskRepository projectTaskRepository,
            CustomerRepository customerRepository) {

        this.projectTaskRepository = projectTaskRepository;
        this.customerRepository = customerRepository;
    }


    // ================================================================
    // ALL PROJECTS
    // ================================================================

    public List<ProjectTask> getAllTasks() {

        return projectTaskRepository
                .findAllByOrderByDueDateAsc();
    }


    // ================================================================
    // PROJECT ANALYTICS
    // ================================================================

    public Map<String, Object> getProjectAnalytics() {

        List<ProjectTask> tasks = getAllTasks();

        long completed =
                countStatus(tasks, "COMPLETED");

        long pending =
                countStatus(tasks, "PENDING");

        long inProgress =
                countStatus(tasks, "IN_PROGRESS");


        List<Map<String, Object>> categorySummary =
                new ArrayList<>();

        for (String category : PROJECT_CATEGORIES) {

            long count = tasks.stream()
                    .filter(task ->
                            category.equalsIgnoreCase(
                                    task.getCategory()
                            )
                    )
                    .count();

            categorySummary.add(
                    Map.of(
                            "category",
                            category,
                            "count",
                            count
                    )
            );
        }


        int currentYear =
                YearMonth.now().getYear();


        List<Map<String, Object>> monthlyCompleted =
                buildMonthlyCounts(
                        tasks,
                        "COMPLETED",
                        currentYear
                );

        List<Map<String, Object>> monthlyPending =
                buildMonthlyCounts(
                        tasks,
                        "PENDING",
                        currentYear
                );

        List<Map<String, Object>> monthlyInProgress =
                buildMonthlyCounts(
                        tasks,
                        "IN_PROGRESS",
                        currentYear
                );

        List<Map<String, Object>> monthlyProjectCounts =
                buildMonthlyProjectCounts(
                        tasks,
                        currentYear
                );


        Map<String, Object> analytics =
                new LinkedHashMap<>();

        analytics.put(
                "completedProjects",
                completed
        );

        analytics.put(
                "pendingProjects",
                pending
        );

        analytics.put(
                "inProgressProjects",
                inProgress
        );

        analytics.put(
                "categorySummary",
                categorySummary
        );

        analytics.put(
                "monthlyCompleted",
                monthlyCompleted
        );

        analytics.put(
                "monthlyPending",
                monthlyPending
        );

        analytics.put(
                "monthlyInProgress",
                monthlyInProgress
        );

        analytics.put(
                "monthlyProjectCounts",
                monthlyProjectCounts
        );

        analytics.put(
                "totalProjectValue",
                tasks.stream()
                        .map(ProjectTask::getAmount)
                        .filter(amount -> amount != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        )
        );

        return analytics;
    }


    // ================================================================
    // CUSTOMER DASHBOARD DATA
    // ================================================================

    public Map<String, Object> getCustomerDashboardData(
            Long customerId) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer not found"
                                )
                        );


        List<ProjectTask> tasks =
                projectTaskRepository
                        .findByCustomerOrderByDueDateAsc(
                                customer
                        );


        long completed =
                countStatus(tasks, "COMPLETED");

        long pending =
                countStatus(tasks, "PENDING");


        List<Map<String, Object>> monthlyPerformance =
                buildMonthlyCounts(
                        tasks,
                        "COMPLETED",
                        YearMonth.now().getYear()
                );


        List<Map<String, Object>> taskSummary =
                new ArrayList<>();


        for (ProjectTask task :
                tasks.stream().limit(6).toList()) {

            Map<String, Object> summary =
                    new LinkedHashMap<>();

            summary.put(
                    "id",
                    task.getId()
            );

            summary.put(
                    "title",
                    task.getTitle()
            );

            summary.put(
                    "category",
                    task.getCategory()
            );

            summary.put(
                    "status",
                    task.getStatus()
            );

            summary.put(
                    "dueDate",
                    task.getDueDate()
            );

            summary.put(
                    "completionDate",
                    task.getCompletionDate()
            );

            summary.put(
                    "report",
                    task.getReport()
            );

            summary.put(
                    "amount",
                    task.getAmount() == null
                            ? BigDecimal.ZERO
                            : task.getAmount()
            );

            taskSummary.add(summary);
        }


        Map<String, Object> customerData =
                new LinkedHashMap<>();

        customerData.put(
                "customerId",
                customer.getId()
        );

        customerData.put(
                "customerName",
                customer.getFullName()
        );

        customerData.put(
                "completedTasks",
                completed
        );

        customerData.put(
                "pendingTasks",
                pending
        );

        customerData.put(
                "monthlyPerformance",
                monthlyPerformance
        );

        customerData.put(
                "tasks",
                taskSummary
        );

        return customerData;
    }


    // ================================================================
    // SAVE PROJECT
    // ================================================================

    public ProjectTask save(ProjectTask projectTask) {

        return projectTaskRepository.save(projectTask);
    }


    // ================================================================
    // CUSTOMER PROJECTS
    // ================================================================

    public List<ProjectResponse> getCustomerProjects(
            Customer customer) {

        return projectTaskRepository
                .findByCustomerOrderByDueDateAsc(customer)
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }


    // ================================================================
    // SINGLE CUSTOMER PROJECT
    // ================================================================

    public ProjectResponse getCustomerProject(
            Long id,
            Customer customer) {

        return projectTaskRepository
                .findByIdAndCustomer(id, customer)
                .map(ProjectResponse::from)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project was not found for this customer"
                        )
                );
    }


    // ================================================================
    // CUSTOMER PROJECT REGISTRATION
    // ================================================================

    public ProjectResponse registerCustomerProject(
            Customer customer,
            ProjectRegistrationRequest request) {

        ProjectTask project = new ProjectTask();

        project.setTitle(
                request.getTitle().trim()
        );

        project.setCategory(
                request.getCategory().trim()
        );

        project.setStatus("PENDING");

        project.setDueDate(
                request.getDueDate()
        );

        project.setReport(
                request.getRequirements() == null
                        ? null
                        : request.getRequirements().trim()
        );

        project.setAmount(
                request.getBudget()
        );

        project.setBudget(
                request.getBudget()
        );

        project.setAdvanceAmount(
                BigDecimal.ZERO
        );


        // Customer information
        project.setClientName(
                customer.getFullName()
        );

        project.setClientEmail(
                customer.getEmail()
        );

        project.setClientPhone(
                customer.getCountryCode()
                        + " "
                        + customer.getPhoneNumber()
        );

        project.setClientAddress(
                customer.getAddress()
        );


        // Link the actual project to the customer.
        project.setCustomer(customer);


        return ProjectResponse.from(
                projectTaskRepository.save(project)
        );
    }



    // ================================================================
    // ADMIN PROJECT STATUS
    // ================================================================

    public ProjectTask updateProjectStatus(Long projectId, String requestedStatus) {
        String status = requestedStatus == null
                ? ""
                : requestedStatus.trim().toUpperCase().replace(' ', '_');

        if (!List.of("PENDING", "IN_PROGRESS", "COMPLETED").contains(status)) {
            throw new IllegalArgumentException(
                    "Project status must be PENDING, IN_PROGRESS, or COMPLETED"
            );
        }

        ProjectTask project = projectTaskRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        project.setStatus(status);
        project.setCompletionDate(
                "COMPLETED".equals(status) ? LocalDate.now() : null
        );

        return projectTaskRepository.save(project);
    }

    // ================================================================
    // STATUS COUNT
    // ================================================================

    private long countStatus(
            List<ProjectTask> tasks,
            String status) {

        return tasks.stream()
                .filter(task ->
                        status.equalsIgnoreCase(
                                task.getStatus()
                        )
                )
                .count();
    }


    // ================================================================
    // MONTHLY STATUS COUNTS
    // ================================================================

    private List<Map<String, Object>> buildMonthlyCounts(
            List<ProjectTask> tasks,
            String targetStatus,
            int year) {

        List<Map<String, Object>> monthlyCounts =
                new ArrayList<>();


        for (int month = 1; month <= 12; month++) {

            final int monthNumber = month;
            final String statusFilter = targetStatus;


            String label =
                    YearMonth.of(year, month)
                            .getMonth()
                            .name()
                            .substring(0, 3);


            long count = tasks.stream()

                    .filter(task ->
                            statusFilter.equalsIgnoreCase(
                                    task.getStatus()
                            )
                    )

                    .filter(task ->
                            task.getCompletionDate() != null
                                    || task.getDueDate() != null
                    )

                    .filter(task -> {

                        LocalDate date =
                                "COMPLETED"
                                        .equalsIgnoreCase(
                                                statusFilter
                                        )
                                        ? task.getCompletionDate()
                                        : task.getDueDate();

                        return date != null
                                && date.getYear() == year
                                && date.getMonthValue()
                                == monthNumber;
                    })

                    .count();


            monthlyCounts.add(
                    Map.of(
                            "month",
                            label,
                            "count",
                            count
                    )
            );
        }

        return monthlyCounts;
    }


    // ================================================================
    // MONTHLY PROJECT COUNTS
    // ================================================================

    private List<Map<String, Object>> buildMonthlyProjectCounts(
            List<ProjectTask> tasks,
            int year) {

        List<Map<String, Object>> monthlyCounts =
                new ArrayList<>();


        for (int month = 1; month <= 12; month++) {

            final int monthNumber = month;


            long count = tasks.stream()
                    .filter(task -> {

                        LocalDate date =
                                task.getRegisteredAt() == null
                                        ? task.getDueDate()
                                        : task.getRegisteredAt()
                                                .toLocalDate();

                        if (date == null) {
                            date = task.getCompletionDate();
                        }

                        return date != null
                                && date.getYear() == year
                                && date.getMonthValue()
                                == monthNumber;
                    })
                    .count();


            monthlyCounts.add(
                    Map.of(
                            "month",
                            YearMonth.of(year, month)
                                    .getMonth()
                                    .name()
                                    .substring(0, 3),

                            "count",
                            count
                    )
            );
        }

        return monthlyCounts;
    }
}
