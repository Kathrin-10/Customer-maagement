package dynamicdudes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dynamicdudes.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Customer> findByEmailIgnoreCase(String email);

    Optional<Customer> findByUsernameIgnoreCase(String username);

    Optional<Customer> findByUsername(String username);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    long countByRole(String role);
}
