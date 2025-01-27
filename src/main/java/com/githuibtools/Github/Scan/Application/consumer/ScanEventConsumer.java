package com.githuibtools.Github.Scan.Application.consumer;


import com.githuibtools.Github.Scan.Application.model.Credential;
import com.githuibtools.Github.Scan.Application.model.ScanEvent;
import com.githuibtools.Github.Scan.Application.model.ScanResult;
import com.githuibtools.Github.Scan.Application.repository.CredentialRepository;
import com.githuibtools.Github.Scan.Application.service.ElasticsearchService;
import com.githuibtools.Github.Scan.Application.service.GitHubScanService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScanEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ScanEventConsumer.class);

    private final CredentialRepository credentialRepository;
    private final GitHubScanService gitHubScanService;
    private final ElasticsearchService elasticsearchService;

    // Constructor injection (replaces @RequiredArgsConstructor)
    public ScanEventConsumer(CredentialRepository credentialRepository,
                             GitHubScanService gitHubScanService,
                             ElasticsearchService elasticsearchService) {
        this.credentialRepository = credentialRepository;
        this.gitHubScanService = gitHubScanService;
        this.elasticsearchService = elasticsearchService;
    }

    @KafkaListener(topics = "${app.kafka.topics.ingestion}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, ScanEvent> record) {
        ScanEvent event = record.value();
        log.info("Received ScanEvent from Kafka: {}", event);

        // 1. Lookup credentials
        Credential credential = credentialRepository.findByOwnerAndRepo(event.getOwner(), event.getRepo());
        if (credential == null) {
            log.error("No credentials found for owner={}, repo={}", event.getOwner(), event.getRepo());
            return;
        }

        // 2. Call GitHub with the retrieved PAT
        String rawResult = gitHubScanService.performScan(credential.getPat(), event);

        // 3. Store in Elasticsearch
        ScanResult scanResult = new ScanResult();
        scanResult.setId(UUID.randomUUID().toString());
        scanResult.setOwner(event.getOwner());
        scanResult.setRepo(event.getRepo());
        scanResult.setTypes(event.getTypes());
        scanResult.setParameters(event.getParameters());
        scanResult.setRawResult(rawResult);

        elasticsearchService.saveScanResult(scanResult);
        log.info("Saved ScanResult to Elasticsearch with ID={}", scanResult.getId());
    }
}
