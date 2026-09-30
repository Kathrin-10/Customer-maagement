package dynamicdudes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import dynamicdudes.model.Customer;
import dynamicdudes.model.ProjectTask;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findAllByOrderByDueDateAsc();

    List<ProjectTask> findByCustomerOrderByDueDateAsc(Customer customer);

    Optional<ProjectTask> findByIdAndCustomer(Long id, Customer customer);

    @Modifying
    @Transactional
    @Query("""
            delete from ProjectTask task
            where task.title like 'Client Project %'
              and task.report like 'Client delivery report for project %'
            """)
    int deleteGeneratedDemoProjects();

    @Modifying
    @Transactional
    @Query("""
            update ProjectTask task
            set task.customer = null
            where task.customer = :customer
            """)
    int clearCustomer(Customer customer);
}
