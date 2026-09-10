package com.bis.assistant.repository;

import com.bis.assistant.model.TestingRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestingRequirementRepository extends JpaRepository<TestingRequirement, Long> {
    List<TestingRequirement> findByStandardId(Long standardId);
}
