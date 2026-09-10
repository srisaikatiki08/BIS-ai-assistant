package com.bis.assistant.controller;

import com.bis.assistant.dto.*;
import com.bis.assistant.service.BISCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:5173", "http://127.0.0.1:5173",
        "http://localhost:5174", "http://127.0.0.1:5174",
        "http://localhost:5175", "http://127.0.0.1:5175",
        "http://localhost:3000", "http://127.0.0.1:3000"
}, allowCredentials = "true")
public class CatalogController {

    private final BISCatalogService catalogService;

    public CatalogController(BISCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/services")
    public ResponseEntity<List<BISServiceDTO>> getServices() {
        return ResponseEntity.ok(catalogService.getAllServices());
    }

    @GetMapping("/certification-schemes")
    public ResponseEntity<List<CertificationSchemeDTO>> getCertificationSchemes() {
        return ResponseEntity.ok(catalogService.getAllSchemes());
    }

    @GetMapping("/laboratories")
    public ResponseEntity<List<LaboratoryDTO>> getLaboratories() {
        return ResponseEntity.ok(catalogService.getAllLaboratories());
    }

    @GetMapping("/updates")
    public ResponseEntity<List<BISUpdateDTO>> getUpdates() {
        return ResponseEntity.ok(catalogService.getAllUpdates());
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getProducts() {
        return ResponseEntity.ok(catalogService.getAllProducts());
    }

    @GetMapping("/hallmarking")
    public ResponseEntity<List<HallmarkingInfoDTO>> getHallmarkingInfo() {
        return ResponseEntity.ok(catalogService.getAllHallmarkingInfo());
    }
}
