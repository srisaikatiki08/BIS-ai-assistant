package com.bis.assistant.service;

import com.bis.assistant.dto.StandardDTO;
import com.bis.assistant.dto.StandardDocumentDTO;
import com.bis.assistant.dto.TestingRequirementDTO;
import com.bis.assistant.model.Standard;
import com.bis.assistant.repository.StandardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StandardService {

    private final StandardRepository standardRepository;

    public StandardService(StandardRepository standardRepository) {
        this.standardRepository = standardRepository;
    }

    @Transactional(readOnly = true)
    public List<StandardDTO> getAllStandards() {
        return standardRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<StandardDTO> getStandardById(Long id) {
        return standardRepository.findById(id).map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<StandardDTO> getStandardByCode(String code) {
        return standardRepository.findByStandardCode(code).map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public List<StandardDTO> searchStandards(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStandards();
        }
        return standardRepository.searchStandards(query.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StandardDTO> getStandardsByCategory(String category) {
        return standardRepository.findByCategoryIgnoreCase(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StandardDTO mapToDTO(Standard entity) {
        if (entity == null) return null;
        StandardDTO dto = new StandardDTO();
        dto.setId(entity.getId());
        dto.setStandardCode(entity.getStandardCode());
        dto.setIsNumber(entity.getIsNumber());
        dto.setTitle(entity.getTitle());
        dto.setDivision(entity.getDivision());
        dto.setCategory(entity.getCategory());
        dto.setYear(entity.getYear());
        dto.setStatus(entity.getStatus());
        dto.setIsMandatory(entity.getIsMandatory());
        dto.setScheme(entity.getScheme());
        dto.setQcoOrder(entity.getQcoOrder());
        dto.setScope(entity.getScope());
        dto.setLicensingProcess(entity.getLicensingProcess());
        dto.setFeeCategory(entity.getFeeCategory());

        if (entity.getDocuments() != null) {
            dto.setDocuments(entity.getDocuments().stream().map(doc ->
                    new StandardDocumentDTO(doc.getId(), doc.getClauseNumber(), doc.getTitle(), doc.getDescription(), doc.getSourceUrl())
            ).collect(Collectors.toList()));
        }

        if (entity.getTestingRequirements() != null) {
            dto.setTestingRequirements(entity.getTestingRequirements().stream().map(tr ->
                    new TestingRequirementDTO(tr.getId(), tr.getParameter(), tr.getTestMethod(), tr.getAcceptanceLimit())
            ).collect(Collectors.toList()));
        }

        if (entity.getApplicableProducts() != null) {
            dto.setApplicableProducts(new ArrayList<>(entity.getApplicableProducts()));
        }

        return dto;
    }
}
