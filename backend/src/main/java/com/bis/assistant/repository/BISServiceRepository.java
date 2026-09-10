package com.bis.assistant.repository;

import com.bis.assistant.model.BISService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BISServiceRepository extends JpaRepository<BISService, Long> {
    Optional<BISService> findByServiceCode(String serviceCode);
    List<BISService> findByCategoryIgnoreCase(String category);
}
