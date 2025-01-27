package com.githuibtools.Github.Scan.Application.controller;

import com.githuibtools.Github.Scan.Application.model.ScanResult;
import com.githuibtools.Github.Scan.Application.repository.ScanResultRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScanResultController {

    private final ScanResultRepository scanResultRepository;

    // Manually defined constructor instead of using Lombok @RequiredArgsConstructor
    public ScanResultController(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    @GetMapping("/api/results")
    public List<ScanResult> getResults(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String repo) {

        // For demonstration, we’ll just fetch all and filter in-memory.
        // In real usage, define custom queries in your repository if needed.
        Iterable<ScanResult> allResults = scanResultRepository.findAll();
        List<ScanResult> filtered = new ArrayList<>();

        for (ScanResult sr : allResults) {
            boolean matchOwner = (owner == null || sr.getOwner().equals(owner));
            boolean matchRepo = (repo == null || sr.getRepo().equals(repo));

            if (matchOwner && matchRepo) {
                filtered.add(sr);
            }
        }
        return filtered;
    }
}

