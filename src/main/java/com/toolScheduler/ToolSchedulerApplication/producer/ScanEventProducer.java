package com.toolScheduler.ToolSchedulerApplication.producer;

import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScanEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEventProducer.class);

    @Value("${app.kafka.topics.scan}")
    private String scanTopic;

    private final KafkaTemplate<String, ScanEvent> scanEventKafkaTemplate;

    public ScanEventProducer(KafkaTemplate<String, ScanEvent> scanEventKafkaTemplate) {
        this.scanEventKafkaTemplate = scanEventKafkaTemplate;
    }

    @PostConstruct
    public void produceDummyEvent() {
        ScanEvent event = new ScanEvent();
        event.setOwner("kritik05");
        event.setRepo("juice-shop");
//      event.setTypes(List.of(ScanType.CODE_SCAN));
        event.setTypes(List.of(ScanType.ALL));

        scanEventKafkaTemplate.send(scanTopic, event);
        LOGGER.info("Produced dummy ScanEvent to topic {}: {}", scanTopic, event);
    }
}
