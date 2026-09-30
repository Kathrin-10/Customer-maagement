package dynamicdudes.service;

import java.util.List;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dynamicdudes.dto.CustomerUpdateRequest;
import dynamicdudes.dto.RegisterRequest;
import dynamicdudes.exception.EmailAlreadyRegisteredException;
import dynamicdudes.exception.UsernameAlreadyRegisteredException;
import dynamicdudes.model.Customer;
import dynamicdudes.repository.CustomerRepository;
import dynamicdudes.repository.ProjectTaskRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectTaskRepository projectTaskRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            ProjectTaskRepository projectTaskRepository) {

        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectTaskRepository = projectTaskRepository;
    }


    // ================================================================
    // CUSTOMER REGISTRATION
    // ================================================================

    public Customer registerCustomer(RegisterRequest request) {

        String username = normalizeUsername(request.getUsername());
        String email = normalizeEmail(request.getEmail());
        String phoneNumber = normalizePhone(request.getPhoneNumber());
        String countryCode = normalizeCountryCode(request.getCountryCode());
        String district = normalizeDistrict(request.getDistrict());

        if (customerRepository.existsByUsernameIgnoreCase(username)) {
            throw new UsernameAlreadyRegisteredException();
        }

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        Customer customer = new Customer();

        customer.setUsername(username);
        customer.setFullName(request.getFullName().trim());
        customer.setEmail(email);
        customer.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        customer.setPhoneNumber(phoneNumber);
        customer.setCountryCode(countryCode);
        customer.setDistrict(district);
        customer.setAddress(
                request.getAddress() == null
                        ? null
                        : request.getAddress().trim()
        );

        customer.setRole("CUSTOMER");

        return customerRepository.save(customer);
    }


    // ================================================================
    // FIND CUSTOMER BY EMAIL
    // ================================================================

    public Customer findByEmail(String email) {

        return customerRepository
                .findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account was not found"
                        )
                );
    }


    // ================================================================
    // FIND CUSTOMER BY USERNAME
    // ================================================================

    public Customer findByUsername(String username) {

        return customerRepository
                .findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account was not found"
                        )
                );
    }


    // ================================================================
    // FIND CUSTOMER BY USERNAME OR EMAIL
    // ================================================================

    public Customer findByUsernameOrEmail(String identifier) {

        String value =
                identifier == null
                        ? ""
                        : identifier.trim();

        return customerRepository
                .findByUsernameIgnoreCase(value)
                .or(() ->
                        customerRepository.findByEmailIgnoreCase(
                                normalizeEmail(value)
                        )
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account was not found"
                        )
                );
    }


    // ================================================================
    // FIND ALL CUSTOMERS
    // ================================================================

    public List<Customer> findAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .filter(customer ->
                        !"ADMIN".equalsIgnoreCase(customer.getRole()))
                .toList();
    }


    // ================================================================
    // COUNT CUSTOMERS
    // ================================================================

    public long countCustomers() {
        return customerRepository.countByRole("CUSTOMER");
    }

    public String getCustomerStatus(Customer customer) {
        List<dynamicdudes.model.ProjectTask> projects =
                projectTaskRepository.findByCustomerOrderByDueDateAsc(customer);

        boolean active = projects.stream().anyMatch(project -> {
            String status = project.getStatus();
            boolean unfinished =
                    status != null && !"COMPLETED".equalsIgnoreCase(status);

            java.math.BigDecimal budget = project.getBudget();
            java.math.BigDecimal advance = project.getAdvanceAmount();
            boolean unpaid =
                    budget != null
                    && (advance == null
                        || advance.compareTo(budget) < 0);

            return unfinished || unpaid;
        });

        return active ? "ACTIVE" : "INACTIVE";
    }

    public Map<String, Long> getCustomerStatusCounts() {
        List<Customer> customers = findAllCustomers();

        long active = customers.stream()
                .filter(customer -> "ACTIVE".equals(getCustomerStatus(customer)))
                .count();

        return Map.of(
                "activeCustomers", active,
                "inactiveCustomers", customers.size() - active
        );
    }

    public long countNewCustomersThisMonth() {
        YearMonth current = YearMonth.now();

        return findAllCustomers().stream()
                .filter(customer -> customer.getCreatedAt() != null)
                .filter(customer -> {
                    LocalDateTime created = customer.getCreatedAt();
                    return created.getYear() == current.getYear()
                            && created.getMonthValue() == current.getMonthValue();
                })
                .count();
    }


    // ================================================================
    // UPDATE CUSTOMER
    // ================================================================

    public Customer updateCustomer(
            Long id,
            CustomerUpdateRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account was not found"
                        )
                );

        if ("ADMIN".equalsIgnoreCase(customer.getRole())) {
            throw new IllegalArgumentException(
                    "Admin account cannot be updated through customer management"
            );
        }

        String email = normalizeEmail(request.getEmail());

        if (customerRepository.existsByEmailIgnoreCaseAndIdNot(
                email,
                id)) {

            throw new EmailAlreadyRegisteredException();
        }

        String newUsername =
                normalizeUsername(request.getUsername());

        if (!newUsername.equalsIgnoreCase(
                customer.getUsername())
                && customerRepository
                        .existsByUsernameIgnoreCaseAndIdNot(
                                newUsername,
                                id)) {

            throw new UsernameAlreadyRegisteredException();
        }

        customer.setUsername(newUsername);

        customer.setFullName(
                request.getFullName().trim()
        );

        customer.setEmail(email);

        customer.setPhoneNumber(
                normalizePhone(request.getPhoneNumber())
        );

        customer.setCountryCode(
                normalizeCountryCode(request.getCountryCode())
        );

        customer.setDistrict(
                normalizeDistrict(request.getDistrict())
        );

        customer.setAddress(
                request.getAddress() == null
                        ? null
                        : request.getAddress().trim()
        );

        return customerRepository.save(customer);
    }


    // ================================================================
    // UPDATE CURRENT CUSTOMER PROFILE
    // ================================================================

    public Customer updateCurrentProfile(
            Long id,
            CustomerUpdateRequest request) {

        return updateCustomer(id, request);
    }


    // ================================================================
    // DELETE CUSTOMER
    // ================================================================

    @Transactional
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account was not found"
                        )
                );

        if ("ADMIN".equalsIgnoreCase(customer.getRole())) {
            throw new IllegalArgumentException(
                    "Admin account cannot be deleted from customer management"
            );
        }

        projectTaskRepository.clearCustomer(customer);

        customerRepository.deleteById(id);
    }


    // ================================================================
    // DELETE MULTIPLE CUSTOMERS
    // ================================================================

    public void deleteCustomers(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            deleteCustomer(id);
        }
    }


    // ================================================================
    // NORMALIZATION HELPERS
    // ================================================================

    private String normalizeUsername(String username) {

        return username == null
                ? ""
                : username
                        .trim()
                        .toLowerCase(
                                java.util.Locale.ROOT
                        );
    }


    private String normalizeEmail(String email) {

        if (email == null) {
            return "";
        }

        String trimmed =
                email.trim()
                        .toLowerCase(
                                java.util.Locale.ROOT
                        );

        if (trimmed.isEmpty()) {
            return "";
        }

        if (trimmed.contains("@")) {
            return trimmed;
        }

        return trimmed + "@gmail.com";
    }


    private String normalizePhone(String phoneNumber) {

        return phoneNumber == null
                ? ""
                : phoneNumber.trim();
    }


    private String normalizeCountryCode(
            String countryCode) {

        return countryCode == null
                ? "+1"
                : countryCode.trim();
    }


    private String normalizeDistrict(String district) {

        return district == null
                ? ""
                : district.trim();
    }
}
