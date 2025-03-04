package com.toolScheduler.ToolSchedulerApplication.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolScheduler.ToolSchedulerApplication.event.AcknowledgementEvent;
import com.toolScheduler.ToolSchedulerApplication.event.ParseRequestEvent;
import com.toolScheduler.ToolSchedulerApplication.event.ScanRequestEvent;
import com.toolScheduler.ToolSchedulerApplication.model.*;
import com.toolScheduler.ToolSchedulerApplication.repository.TenantRepository;
import com.toolScheduler.ToolSchedulerApplication.service.GitHubScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Random;

@Component
public class ScanRequestEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanRequestEventConsumer.class);
    private final Random random = new Random();
    private final TenantRepository tenantRepository;
    private final GitHubScanService gitHubScanService;
    private final KafkaTemplate<String, Object> parseJobSend;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.toolscheduler}")
    private String scanTopic;

    @Value("${app.kafka.topics.jfcunified}")
    private String unifiedTopic;

    @Value("${app.kafka.topics.ack}")
    private String ackTopic;

    public ScanRequestEventConsumer(TenantRepository tenantRepository,
                                    GitHubScanService gitHubScanService,
                                    KafkaTemplate<String, Object> parseJobSend,
                                    @Qualifier("parseEventKafkaTemplate")KafkaTemplate<String,String> kafkaTemplate,
                                    ObjectMapper objectMapper) {
        this.tenantRepository = tenantRepository;
        this.gitHubScanService = gitHubScanService;
        this.parseJobSend = parseJobSend;
        this.kafkaTemplate=kafkaTemplate;
        this.objectMapper=objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.toolscheduler}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "scanRequestEventListenerContainerFactory")
    public void consumeScanEvent(ScanRequestEvent event) throws JsonProcessingException {
        String originalEventId = event.getEventId();

        try {
            ScanRequestPayload payload = event.getPayload();
            Integer tenantId = payload.getTenantId();
            String toolType = payload.getTypes();

            Optional<Tenant> optionalTenant = tenantRepository.findById(tenantId);
            if (optionalTenant.isEmpty()) {
                LOGGER.error("No tenant found with ID {}", tenantId);
                return;
            }
            Tenant tenant = optionalTenant.get();
            String rawJson = gitHubScanService.performSingleToolScan(tenant.getPat(), tenant.getOwner(), tenant.getRepo(), toolType);

            String toolFolder = toolType;

            String folderPath = "/Users/kritik.aggarwal/Desktop/scan/" + tenant.getTenant_name() + "/" + toolFolder;

            File dir = new File(folderPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    LOGGER.error("Failed to create directories at: {}", folderPath);
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            String fileName = "scan_" + timestamp + ".json";
            String filePath = folderPath + "/" + fileName;

            try (FileWriter fw = new FileWriter(filePath)) {
                fw.write(rawJson);

                LOGGER.info("Wrote scan data for tool={} to {}", toolType, filePath);
            } catch (IOException e) {
                LOGGER.error("Error writing JSON to file", e);
            }

            AcknowledgementPayload ackPayload = new AcknowledgementPayload(originalEventId, "SUCCESS");
            AcknowledgementEvent ackEvent = new AcknowledgementEvent(null, ackPayload);
            parseJobSend.send(ackTopic, ackEvent);


            int sleepMs = 3000 + random.nextInt(3000);
            Thread.sleep(sleepMs);


            ParseRequestPayload parsePayload = new ParseRequestPayload(tenantId, filePath, toolType);
            ParseRequestEvent parseEvent = new ParseRequestEvent(parsePayload);
            String json = objectMapper.writeValueAsString(parseEvent);
            kafkaTemplate.send(unifiedTopic, json);
        }
        catch (Exception e){
            AcknowledgementPayload ackPayload = new AcknowledgementPayload(originalEventId, "FAIL");
            AcknowledgementEvent ackEvent = new AcknowledgementEvent(null, ackPayload);
            parseJobSend.send(ackTopic, ackEvent);
        }

    }

}

