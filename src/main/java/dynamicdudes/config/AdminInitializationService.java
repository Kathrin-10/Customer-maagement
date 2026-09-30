package dynamicdudes.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dynamicdudes.model.Customer;
import dynamicdudes.repository.CustomerRepository;
import dynamicdudes.repository.ProjectTaskRepository;
import dynamicdudes.repository.RevenueRepository;

@Service
public class AdminInitializationService {

    private final CustomerRepository customerRepository;
    private final RevenueRepository revenueRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final PasswordEncoder passwordEncoder;

    private final String adminUsername;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializationService(
            CustomerRepository customerRepository,
            RevenueRepository revenueRepository,
            ProjectTaskRepository projectTaskRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminPassword) {

        this.customerRepository = customerRepository;
        this.revenueRepository = revenueRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Transactional
    public void initialize() {

        revenueRepository.deleteByProjectIsNull();

        projectTaskRepository.deleteGeneratedDemoProjects();

        Customer existingAdmin = customerRepository
                .findByUsernameIgnoreCase(adminUsername.trim())
                .or(() -> customerRepository.findByEmailIgnoreCase(adminEmail.trim()))
                .orElse(null);

        if (existingAdmin != null) {

            boolean requiresUpdate =
                    !passwordEncoder.matches(
                            adminPassword,
                            existingAdmin.getPassword()
                    )
                    || !"ADMIN".equals(existingAdmin.getRole());

            if (requiresUpdate) {

                existingAdmin.setUsername(
                        adminUsername.trim().toLowerCase(Locale.ROOT)
                );

                existingAdmin.setPassword(
                        passwordEncoder.encode(adminPassword)
                );

                existingAdmin.setRole("ADMIN");

                customerRepository.save(existingAdmin);
            }

        } else {

            Customer admin = new Customer();

            admin.setUsername(
                    adminUsername.trim().toLowerCase(Locale.ROOT)
            );

            admin.setFullName("System Administrator");
            admin.setEmail(adminEmail);

            admin.setPassword(
                    passwordEncoder.encode(adminPassword)
            );

            admin.setPhoneNumber("+1 0000000000");
            admin.setCountryCode("+1");
            admin.setDistrict("Head Office");
            admin.setAddress("System Account");
            admin.setRole("ADMIN");

            customerRepository.save(admin);
        }
    }
}