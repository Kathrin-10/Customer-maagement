package dynamicdudes.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicdudes.dto.CustomerResponse;
import dynamicdudes.dto.RegisterRequest;
import dynamicdudes.model.Customer;
import dynamicdudes.service.CustomerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public Customer register(
            @Valid @RequestBody RegisterRequest request) {

        return customerService.registerCustomer(request);
    }

    @GetMapping("/me")
    public CustomerResponse currentUser(
            Authentication authentication) {

        return CustomerResponse.from(
                customerService.findByUsernameOrEmail(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/csrf")
    public CsrfToken csrfToken(CsrfToken token) {
        return token;
    }
}
