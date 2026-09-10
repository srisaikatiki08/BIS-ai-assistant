package com.bis.assistant.repository;

import com.bis.assistant.model.CertificationScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificationSchemeRepository extends JpaRepository<CertificationScheme, Long> {
    Optional<CertificationScheme> findBySchemeCode(String schemeCode);
}
