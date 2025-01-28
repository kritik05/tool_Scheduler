package com.githuibtools.Github.Scan.Application.consumer;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;
import java.util.UUID;

@Component
public class ScanEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ScanEventConsumer.class);

    private final CredentialRepository credentialRepository;
    private final GitHubScanService gitHubScanService;
    private final ElasticsearchService elasticsearchService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        Credential credential = credentialRepository.findByOwnerAndRepo(event.getOwner(), event.getRepo());
        if (credential == null) {
            log.error("No credentials found for owner={}, repo={}", event.getOwner(), event.getRepo());
            return;
        }

        JsonNode finalResult  = gitHubScanService.performScan(credential.getPat(), event);
        Map<String, Object> resultMap = objectMapper.convertValue(finalResult, new TypeReference<Map<String, Object>>() {});

        ScanResult scanResult = new ScanResult();
        scanResult.setId(UUID.randomUUID().toString());
        scanResult.setOwner(event.getOwner());
        scanResult.setRepo(event.getRepo());
        scanResult.setTypes(event.getTypes());
        scanResult.setParameters(event.getParameters());
        scanResult.setResults(resultMap);

        elasticsearchService.saveScanResult(scanResult);
        log.info("Saved ScanResult to Elasticsearch with ID={}", scanResult.getId());
    }
}
