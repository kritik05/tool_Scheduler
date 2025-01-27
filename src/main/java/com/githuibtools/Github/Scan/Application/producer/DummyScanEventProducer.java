package com.githuibtools.Github.Scan.Application.producer;

import com.githuibtools.Github.Scan.Application.model.ScanParameter;
import com.githuibtools.Github.Scan.Application.model.ScanType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.githuibtools.Github.Scan.Application.model.ScanEvent;

import java.util.List;

@Component
public class DummyScanEventProducer {

    private final KafkaTemplate<String, ScanEvent> kafkaTemplate;

    @Value("${app.kafka.topics.ingestion}")
    private String ingestionTopic;

    // Manually define the constructor instead of using Lombok @RequiredArgsConstructor
    public DummyScanEventProducer(KafkaTemplate<String, ScanEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void produceDummyEvent() {
        // Create a dummy ScanEvent
        ScanEvent event = new ScanEvent();
        event.setRepo("juice-shop");
        event.setOwner("kritik05");
        event.setTypes(List.of(ScanType.ALL));
        event.setParameters(List.of(ScanParameter.FAST, ScanParameter.DEEP));

        // Send it to Kafka
        kafkaTemplate.send(ingestionTopic, event);

        // For demonstration, we do this once at startup.
    }
}
