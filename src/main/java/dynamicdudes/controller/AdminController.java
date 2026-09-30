
package dynamicdudes.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicdudes.dto.CustomerResponse;
import dynamicdudes.dto.CustomerUpdateRequest;
import dynamicdudes.service.CustomerService;

@RestController
@RequestMapping("/api/admin/customers")
public class AdminController {

    private final CustomerService customerService;

    public AdminController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> listCustomers() {
        return customerService.findAllCustomers().stream()
                .filter(customer -> "ACTIVE".equals(customerService.getCustomerStatus(customer)))
                .map(customer -> CustomerResponse.from(customer, "ACTIVE"))
                .toList();
    }

    @GetMapping("/inactive")
    public List<CustomerResponse> listInactiveCustomers() {
        return customerService.findAllCustomers().stream()
                .filter(customer -> "INACTIVE".equals(customerService.getCustomerStatus(customer)))
                .map(customer -> CustomerResponse.from(customer, "INACTIVE"))
                .toList();
    }

    @GetMapping("/report")
    public Map<String, Long> report() {
        Map<String, Long> statusCounts =
                customerService.getCustomerStatusCounts();

        return Map.of(
                "customerCount", customerService.countCustomers(),
                "activeCustomers", statusCounts.get("activeCustomers"),
                "inactiveCustomers", statusCounts.get("inactiveCustomers"),
                "newCustomers", customerService.countNewCustomersThisMonth()
        );
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {

        return CustomerResponse.from(
                customerService.updateCustomer(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PostMapping("/delete")
    public void deleteCustomers(@RequestBody List<Long> ids) {
        customerService.deleteCustomers(ids);
    }
}
