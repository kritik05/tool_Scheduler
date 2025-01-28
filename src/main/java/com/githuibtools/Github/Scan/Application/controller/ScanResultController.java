package com.githuibtools.Github.Scan.Application.controller;

import com.githuibtools.Github.Scan.Application.model.ScanResult;
import com.githuibtools.Github.Scan.Application.repository.ScanResultRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import java.util.ArrayList;
import java.util.*;

@RestController
public class ScanResultController {

    private final ScanResultRepository scanResultRepository;

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

    @GetMapping("/codescanning")
    public List<Map<String, Object>> getCodeScanAlerts() {
        List<Map<String, Object>> codeScanAlertsList = new ArrayList<>();

        for (ScanResult sr : scanResultRepository.findAll()) {
            Map<String, Object> topLevel = sr.getResults();
            Object resultsObj = topLevel.get("results");
            if (resultsObj instanceof Map) {
                Map<String, Object> resultsMap = (Map<String, Object>) resultsObj;
                Object codeAlerts = resultsMap.get("codeScanningAlerts");
                if (codeAlerts != null) {
                    codeScanAlertsList.add(Map.of(
                            "scanResultId", sr.getId(),
                            "owner", sr.getOwner(),
                            "repo", sr.getRepo(),
                            "codeScanningAlerts", codeAlerts
                    ));
                }
            }
        }
        return codeScanAlertsList;
    }

    @GetMapping("/dependabot")
    public List<Map<String, Object>> getDependabotAlerts() {
        List<Map<String, Object>> depAlertsList = new ArrayList<>();

        for (ScanResult sr : scanResultRepository.findAll()) {
            Map<String, Object> topLevel = sr.getResults();
            Object resultsObj = topLevel.get("results");
            if (resultsObj instanceof Map) {
                Map<String, Object> resultsMap = (Map<String, Object>) resultsObj;
                Object depAlerts = resultsMap.get("dependabotAlerts");
                if (depAlerts != null) {
                    depAlertsList.add(Map.of(
                            "scanResultId", sr.getId(),
                            "owner", sr.getOwner(),
                            "repo", sr.getRepo(),
                            "dependabotAlerts", depAlerts
                    ));
                }
            }
        }
        return depAlertsList;
    }

    @GetMapping("/secrets")
    public List<Map<String, Object>> getSecretScanningAlerts() {
        List<Map<String, Object>> secretAlertsList = new ArrayList<>();

        for (ScanResult sr : scanResultRepository.findAll()) {
            Map<String, Object> topLevel = sr.getResults();
            Object resultsObj = topLevel.get("results");
            if (resultsObj instanceof Map) {
                Map<String, Object> resultsMap = (Map<String, Object>) resultsObj;
                Object secretAlerts = resultsMap.get("secretScanningAlerts");
                if (secretAlerts != null) {
                    secretAlertsList.add(Map.of(
                            "scanResultId", sr.getId(),
                            "owner", sr.getOwner(),
                            "repo", sr.getRepo(),
                            "secretScanningAlerts", secretAlerts
                    ));
                }
            }
        }
        return secretAlertsList;
    }
}
