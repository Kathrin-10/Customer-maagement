package dynamicdudes.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.transaction.annotation.Transactional;

import dynamicdudes.model.Customer;
import dynamicdudes.model.ProjectTask;
import dynamicdudes.repository.ProjectTaskRepository;

import org.springframework.stereotype.Service;

import dynamicdudes.model.RevenueRecord;
import dynamicdudes.repository.RevenueRepository;

@Service
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final ProjectTaskRepository projectTaskRepository;

    public RevenueService(RevenueRepository revenueRepository,
                          ProjectTaskRepository projectTaskRepository) {
        this.revenueRepository = revenueRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    public List<RevenueRecord> getAllRevenue() {
        return revenueRepository.findAllByOrderByRecordDateAsc();
    }

    public Map<String, Object> getRevenueSummary() {
        List<RevenueRecord> records = getAllRevenue();
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        // Existing projects may already contain advance_amount values from
        // the original database, while newer payments are stored in
        // revenue_records. Build the report from both without double-counting.
        List<RevenuePoint> points = buildRevenuePoints(records);

        BigDecimal thisMonth = sumForMonth(points, currentMonth);
        BigDecimal lastMonth = sumForMonth(points, previousMonth);
        BigDecimal delta = thisMonth.subtract(lastMonth);
        LocalDate today = LocalDate.now();
        BigDecimal last365Days = points.stream()
                .filter(point -> point.date() != null)
                .filter(point -> !point.date().isBefore(today.minusDays(364)))
                .filter(point -> !point.date().isAfter(today))
                .map(RevenuePoint::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> yearlyRevenue = new ArrayList<>();
        for (int month = 11; month >= 0; month--) {
            YearMonth yearMonth = currentMonth.minusMonths(month);
            BigDecimal total = sumForMonth(points, yearMonth);
            yearlyRevenue.add(Map.of(
                    "month", yearMonth.getMonth().name().substring(0, 3),
                    "amount", total,
                    "monthIndex", 12 - month
            ));
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("thisMonthRevenue", thisMonth);
        summary.put("lastMonthRevenue", lastMonth);
        summary.put("revenueDelta", delta);
        summary.put("yearlyRevenue", yearlyRevenue);
        summary.put("yearlyTotal", last365Days);
        summary.put("last365DaysRevenue", last365Days);
        return summary;
    }

    private List<RevenuePoint> buildRevenuePoints(List<RevenueRecord> records) {
        List<RevenuePoint> points = new ArrayList<>();
        Map<Long, BigDecimal> recordedByProject = new HashMap<>();

        for (RevenueRecord record : records) {
            // Only payments linked to a real project belong in the live report.
            // Older seed/demo revenue rows have no project_id and are ignored.
            if (record.getProject() == null) {
                continue;
            }
            Long projectId = record.getProject().getId();
            BigDecimal amount = record.getAmount() == null ? BigDecimal.ZERO : record.getAmount();
            recordedByProject.merge(projectId, amount, BigDecimal::add);
            points.add(new RevenuePoint(record.getRecordDate(), amount));
        }

        for (ProjectTask project : projectTaskRepository.findAll()) {
            BigDecimal advance = project.getAdvanceAmount();
            if (advance == null || advance.signum() <= 0 || project.getId() == null) {
                continue;
            }

            BigDecimal recorded = recordedByProject.getOrDefault(project.getId(), BigDecimal.ZERO);
            BigDecimal legacyBalance = advance.subtract(recorded);
            if (legacyBalance.signum() > 0) {
                LocalDate date = project.getLastPaymentDate();
                if (date == null && project.getRegisteredAt() != null) {
                    date = project.getRegisteredAt().toLocalDate();
                }
                if (date != null) {
                    points.add(new RevenuePoint(date, legacyBalance));
                }
            }
        }

        return points;
    }

    private BigDecimal sumForMonth(List<RevenuePoint> points, YearMonth yearMonth) {
        return points.stream()
                .filter(point -> point.date() != null)
                .filter(point -> YearMonth.from(point.date()).equals(yearMonth))
                .map(RevenuePoint::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private record RevenuePoint(LocalDate date, BigDecimal amount) {}


    @Transactional
    public RevenueRecord recordCustomerPayment(Long projectId, BigDecimal amount, Customer customer) {
        ProjectTask project = projectTaskRepository
                .findByIdAndCustomer(projectId, customer)
                .orElseThrow(() -> new IllegalArgumentException("Project was not found for this customer"));

        BigDecimal budget = project.getBudget() == null ? BigDecimal.ZERO : project.getBudget();
        BigDecimal currentAdvance = project.getAdvanceAmount() == null
                ? BigDecimal.ZERO
                : project.getAdvanceAmount();
        BigDecimal remaining = budget.subtract(currentAdvance);

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (amount.compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Payment cannot be greater than the remaining project balance of " + remaining);
        }

        LocalDate today = LocalDate.now();
        project.setAdvanceAmount(currentAdvance.add(amount));
        project.setLastPaymentDate(today);
        projectTaskRepository.save(project);

        RevenueRecord record = new RevenueRecord();
        record.setAmount(amount);
        record.setRecordDate(today);
        record.setProjectName(project.getTitle());
        record.setCategory(project.getCategory());
        record.setProjectInfo("Payment received from " + customer.getFullName());
        record.setProject(project);

        return revenueRepository.save(record);
    }

    public RevenueRecord saveRevenueRecord(RevenueRecord record) {
        return revenueRepository.save(record);
    }
}
