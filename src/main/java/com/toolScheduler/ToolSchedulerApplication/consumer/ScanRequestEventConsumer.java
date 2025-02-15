package com.toolScheduler.ToolSchedulerApplication.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toolScheduler.ToolSchedulerApplication.event.AcknowledgementEvent;
import com.toolScheduler.ToolSchedulerApplication.event.ParseRequestEvent;
import com.toolScheduler.ToolSchedulerApplication.event.ScanRequestEvent;
import com.toolScheduler.ToolSchedulerApplication.model.*;
import com.toolScheduler.ToolSchedulerApplication.repository.TenantRepository;
import com.toolScheduler.ToolSchedulerApplication.service.GitHubScanService;
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
import java.util.Optional;

@Component
public class ScanRequestEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanRequestEventConsumer.class);

    private final TenantRepository tenantRepository;
    private final GitHubScanService gitHubScanService;
    private final KafkaTemplate<String, Object> parseJobSend;

    @Value("${app.kafka.topics.toolscheduler}")
    private String scanTopic;

    @Value("${app.kafka.topics.parserjfc}")
    private String parseTopic;

    @Value("${app.kafka.topics.ack}")
    private String ackTopic;

    public ScanRequestEventConsumer(TenantRepository tenantRepository,
                                    GitHubScanService gitHubScanService,
                                    KafkaTemplate<String, Object> parseJobSend) {
        this.tenantRepository = tenantRepository;
        this.gitHubScanService = gitHubScanService;
        this.parseJobSend = parseJobSend;
    }

    @KafkaListener(topics = "${app.kafka.topics.toolscheduler}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "scanRequestEventListenerContainerFactory")
    public void consumeScanEvent(ScanRequestEvent event) throws JsonProcessingException {
        ScanRequestPayload payload = event.getPayload();
        Integer tenantId = payload.getTenantId();       // e.g. 123
        // e.g. "Scan_CodeScan", "Scan_SecretScan", etc.
        String typesString = payload.getTypes();

        Optional<Tenant> optionalTenant = tenantRepository.findById(tenantId);
        if (optionalTenant.isEmpty()) {
            LOGGER.error("No tenant found with ID {}",tenantId);
            return;
        }
        Tenant tenant = optionalTenant.get();
        String toolType=typesString;
            String rawJson = gitHubScanService.performSingleToolScan(tenant.getPat(),tenant.getOwner(),tenant.getRepo(), toolType);

            String toolFolder = toolType;

            String folderPath = "/Users/kritik.aggarwal/Desktop/scan/"+ tenant.getTenant_name()+ "/" + toolFolder;

            File dir = new File(folderPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    LOGGER.error("Failed to create directories at: {}", folderPath);
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
            }
        String originalEventId = event.getEventId();
        AcknowledgementPayload ackPayload = new AcknowledgementPayload(originalEventId, "SUCCESS");
        AcknowledgementEvent ackEvent = new AcknowledgementEvent(null, ackPayload);
        parseJobSend.send(ackTopic, ackEvent);


        ParseRequestPayload parsePayload = new ParseRequestPayload(tenantId, filePath, toolType);
        ParseRequestEvent parseEvent = new ParseRequestEvent(parsePayload);
        parseJobSend.send(parseTopic, parseEvent);

    }

}

