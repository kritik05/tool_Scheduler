package com.toolScheduler.ToolSchedulerApplication.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toolScheduler.ToolSchedulerApplication.model.ScanType;
import com.toolScheduler.ToolSchedulerApplication.model.Tenant;
import com.toolScheduler.ToolSchedulerApplication.model.FileLocationEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.repository.TenantRepository;
import com.toolScheduler.ToolSchedulerApplication.service.GitHubScanService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class ScanEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEventConsumer.class);

    private final TenantRepository tenantRepository;
    private final GitHubScanService gitHubScanService;
    private final KafkaTemplate<String, FileLocationEvent> fileLocationProducer;

    @Value("${app.kafka.topics.scan}")
    private String scanTopic;

    @Value("${app.kafka.topics.filelocation}")
    private String fileLocationTopic;

    public ScanEventConsumer(TenantRepository tenantRepository,
                             GitHubScanService gitHubScanService,
                             KafkaTemplate<String, FileLocationEvent> fileLocationProducer) {
        this.tenantRepository = tenantRepository;
        this.gitHubScanService = gitHubScanService;
        this.fileLocationProducer = fileLocationProducer;
    }

    @KafkaListener(topics = "${app.kafka.topics.scan}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "scanEventListenerContainerFactory")
    public void consumeScanEvent(ConsumerRecord<String, ScanEvent> record) throws JsonProcessingException {
        ScanEvent event = record.value();
        LOGGER.info("Received ScanEvent: {}", event);

        Optional<Tenant> optionalTenant = tenantRepository.findById(event.getTenantId());
        if (optionalTenant.isEmpty()) {
            LOGGER.error("No tenant found with ID {}", event.getTenantId());
            return;
        }
        Tenant tenant = optionalTenant.get();
        List<ScanType> effectiveTypes = expandTypes(event.getTypes());

        for (ScanType toolType : effectiveTypes) {

            String rawJson = gitHubScanService.performSingleToolScan(tenant.getPat(),tenant.getOwner(),tenant.getRepo(), toolType);

            String toolFolder = mapToolFolder(toolType);

            String folderPath = "/Users/kritik.aggarwal/Desktop/scan/"+ tenant.getTenant_name()+ "/" + toolFolder;

            File dir = new File(folderPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    LOGGER.error("Failed to create directories at: {}", folderPath);
                    continue;
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            String fileName = "scan_"+ timestamp + ".json";
            String filePath = folderPath + "/" + fileName;

            try (FileWriter fw = new FileWriter(filePath)) {
                fw.write(rawJson);
                LOGGER.info("Wrote scan data for tool={} to {}", toolType, filePath);
            } catch (IOException e) {
                LOGGER.error("Error writing JSON to file", e);
                continue;
            }

            FileLocationEvent fle = new FileLocationEvent(filePath, toolFolder, tenant.getFindingindex());
            fileLocationProducer.send(fileLocationTopic, fle);
            LOGGER.info("Published FileLocationEvent => [filePath={}, toolName={}]", filePath, toolFolder);
        }
    }


    private List<ScanType> expandTypes(List<ScanType> incomingTypes) {
        if (incomingTypes == null || incomingTypes.isEmpty()) {
            return List.of(); // no types => do nothing
        }
        if (incomingTypes.contains(ScanType.ALL)) {
            return List.of(ScanType.CODE_SCAN, ScanType.DEPENDABOT, ScanType.SECRET_SCAN);
        }
        return incomingTypes;
    }


    private String mapToolFolder(ScanType t) {
        return switch (t) {
            case CODE_SCAN -> "codescan";
            case DEPENDABOT -> "dependabot";
            case SECRET_SCAN -> "secretscan";
            default -> "mixed";
        };
    }
}

