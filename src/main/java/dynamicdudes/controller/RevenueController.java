package dynamicdudes.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicdudes.service.RevenueService;

@RestController
@RequestMapping("/api/admin")
public class RevenueController {

    private final RevenueService revenueService;
    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/revenue")
    public Map<String, Object> getRevenue() {
        return Map.of("records", revenueService.getAllRevenue());
    }

    @GetMapping("/revenue/summary")
    public Map<String, Object> getRevenueSummary() {
        return revenueService.getRevenueSummary();
    }
}
