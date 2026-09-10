package com.bis.assistant.repository;

import com.bis.assistant.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    List<Laboratory> findByCityIgnoreCase(String city);
    List<Laboratory> findByTypeIgnoreCase(String type);
}
