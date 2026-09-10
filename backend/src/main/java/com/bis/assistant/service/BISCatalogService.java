package com.bis.assistant.service;

import com.bis.assistant.dto.*;
import com.bis.assistant.model.*;
import com.bis.assistant.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BISCatalogService {

    private final CertificationSchemeRepository schemeRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final BISServiceRepository serviceRepository;
    private final HallmarkingInfoRepository hallmarkingRepository;
    private final BISUpdateRepository updateRepository;
    private final ProductRepository productRepository;

    public BISCatalogService(CertificationSchemeRepository schemeRepository,
                             LaboratoryRepository laboratoryRepository,
                             BISServiceRepository serviceRepository,
                             HallmarkingInfoRepository hallmarkingRepository,
                             BISUpdateRepository updateRepository,
                             ProductRepository productRepository) {
        this.schemeRepository = schemeRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.serviceRepository = serviceRepository;
        this.hallmarkingRepository = hallmarkingRepository;
        this.updateRepository = updateRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CertificationSchemeDTO> getAllSchemes() {
        return schemeRepository.findAll().stream()
                .map(s -> new CertificationSchemeDTO(s.getId(), s.getSchemeCode(), s.getName(),
                        s.getMarkName(), s.getTargetGroup(), s.getDescription(),
                        s.getAuditRequirement(), s.getPortalUrl()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LaboratoryDTO> getAllLaboratories() {
        return laboratoryRepository.findAll().stream()
                .map(l -> new LaboratoryDTO(l.getId(), l.getName(), l.getType(), l.getCity(),
                        l.getState(), l.getAddress(), l.getGoogleMapsDirectionsUrl(),
                        l.getNablAccreditation(), l.getContactPerson(),
                        l.getPhone(), l.getEmail(), l.getRecognizedStandards()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BISServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(s -> new BISServiceDTO(s.getId(), s.getServiceCode(), s.getTitle(),
                        s.getCategory(), s.getDescription(), s.getTargetBeneficiary(),
                        s.getKeyFeatures(), s.getPortalUrl()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HallmarkingInfoDTO> getAllHallmarkingInfo() {
        return hallmarkingRepository.findAll().stream()
                .map(h -> new HallmarkingInfoDTO(h.getId(), h.getTitle(), h.getStandardReference(),
                        h.getHuidDigits(), h.getDescription(), h.getApplicableMetals(),
                        h.getRegistrationProcess(), h.getConsumerVerificationSteps()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BISUpdateDTO> getAllUpdates() {
        return updateRepository.findAll().stream()
                .map(u -> new BISUpdateDTO(u.getId(), u.getTitle(), u.getDate(), u.getCategory(),
                        u.getGazetteNumber(), u.getEffectiveDate(), u.getSummary(),
                        u.getAffectedStandards(), u.getSourceUrl()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(p -> {
            ProductDTO dto = new ProductDTO(p.getId(), p.getName(), p.getCategory(),
                    p.getDescription(), p.getIntendedUse(), p.getMaterial());
            if (p.getStandards() != null) {
                dto.setStandardCodes(p.getStandards().stream()
                        .map(Standard::getStandardCode)
                        .collect(Collectors.toList()));
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
