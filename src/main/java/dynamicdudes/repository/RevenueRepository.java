package dynamicdudes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dynamicdudes.model.RevenueRecord;

public interface RevenueRepository extends JpaRepository<RevenueRecord, Long> {

    List<RevenueRecord> findAllByOrderByRecordDateAsc();

    boolean existsByProject_Id(Long projectId);

    long deleteByProjectIsNull();
}
