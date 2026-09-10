package com.bis.assistant.repository;

import com.bis.assistant.model.HallmarkingInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallmarkingInfoRepository extends JpaRepository<HallmarkingInformation, Long> {
}
