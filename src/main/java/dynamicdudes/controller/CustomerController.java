package dynamicdudes.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicdudes.dto.CustomerResponse;
import dynamicdudes.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public CustomerResponse profile(Authentication authentication) {
        var customer = customerService.findByUsernameOrEmail(authentication.getName());
        return CustomerResponse.from(customer, customerService.getCustomerStatus(customer));
    }
}
