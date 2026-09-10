package com.bis.assistant.repository;

import com.bis.assistant.model.BISUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BISUpdateRepository extends JpaRepository<BISUpdate, Long> {
    List<BISUpdate> findByCategoryIgnoreCase(String category);
}
