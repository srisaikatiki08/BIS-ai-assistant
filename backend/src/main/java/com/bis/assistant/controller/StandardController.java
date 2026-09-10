package com.bis.assistant.controller;

import com.bis.assistant.dto.StandardDTO;
import com.bis.assistant.service.StandardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standards")
@CrossOrigin(origins = {
        "http://localhost:5173", "http://127.0.0.1:5173",
        "http://localhost:5174", "http://127.0.0.1:5174",
        "http://localhost:5175", "http://127.0.0.1:5175",
        "http://localhost:3000", "http://127.0.0.1:3000"
}, allowCredentials = "true")
public class StandardController {

    private final StandardService standardService;

    public StandardController(StandardService standardService) {
        this.standardService = standardService;
    }

    @GetMapping
    public ResponseEntity<List<StandardDTO>> getAllStandards() {
        return ResponseEntity.ok(standardService.getAllStandards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardDTO> getStandardById(@PathVariable("id") Long id) {
        return standardService.getStandardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<StandardDTO> getStandardByCode(@PathVariable("code") String code) {
        return standardService.getStandardByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<StandardDTO>> searchStandards(@RequestParam(name = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(standardService.searchStandards(query));
    }
}
