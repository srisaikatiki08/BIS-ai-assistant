package com.bis.assistant.repository;

import com.bis.assistant.model.Standard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Long> {

    Optional<Standard> findByStandardCode(String standardCode);

    List<Standard> findByCategoryIgnoreCase(String category);

    List<Standard> findByIsMandatory(Boolean isMandatory);

    @Query("SELECT s FROM Standard s WHERE " +
           "LOWER(s.isNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.standardCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.scope) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Standard> searchStandards(@Param("query") String query);
}
