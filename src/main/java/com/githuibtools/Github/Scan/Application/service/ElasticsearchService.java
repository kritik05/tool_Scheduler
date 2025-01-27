package com.githuibtools.Github.Scan.Application.service;

import com.githuibtools.Github.Scan.Application.model.ScanResult;
import com.githuibtools.Github.Scan.Application.repository.ScanResultRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ElasticsearchService {

    private final ScanResultRepository scanResultRepository;

    // Constructor injection (manually defined, instead of using Lombok @RequiredArgsConstructor)
    public ElasticsearchService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    public ScanResult saveScanResult(ScanResult scanResult) {
        return scanResultRepository.save(scanResult);
    }

    public Optional<ScanResult> findById(String id) {
        return scanResultRepository.findById(id);
    }

    // Add custom search methods if needed
    // e.g. findByOwnerAndRepo, etc.
}