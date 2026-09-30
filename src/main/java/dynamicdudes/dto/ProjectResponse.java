package dynamicdudes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import dynamicdudes.model.ProjectTask;

public record ProjectResponse(
        Long id,
        String title,
        String category,
        String status,
        LocalDate dueDate,
        LocalDate completionDate,
        LocalDateTime registeredAt,
        String report,
        BigDecimal amount,
        BigDecimal budget,
        BigDecimal advanceAmount,
        LocalDate lastPaymentDate,
        String assignedCustomerName) {

    public static ProjectResponse from(ProjectTask project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getCategory(),
                project.getStatus(),
                project.getDueDate(),
                project.getCompletionDate(),
                project.getRegisteredAt(),
                project.getReport(),
                project.getAmount(),
                project.getBudget(),
                project.getAdvanceAmount(),
                project.getLastPaymentDate(),
                project.getCustomer() == null
                        ? null
                        : project.getCustomer().getFullName()
        );
    }
}
