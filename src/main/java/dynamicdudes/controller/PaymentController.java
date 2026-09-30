package dynamicdudes.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicdudes.dto.PaymentRequest;
import dynamicdudes.service.CustomerService;
import dynamicdudes.service.RevenueService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/payments")
public class PaymentController {

    private final RevenueService revenueService;
    private final CustomerService customerService;

    public PaymentController(RevenueService revenueService, CustomerService customerService) {
        this.revenueService = revenueService;
        this.customerService = customerService;
    }

    @PostMapping
    public Map<String, Object> recordPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        var customer = customerService.findByUsernameOrEmail(authentication.getName());
        var record = revenueService.recordCustomerPayment(
                request.projectId(),
                request.amount(),
                customer
        );

        return Map.of(
                "message", "Payment recorded successfully.",
                "paymentId", record.getId(),
                "amount", record.getAmount()
        );
    }
}
