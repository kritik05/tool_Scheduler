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

    public DummyScanEventProducer(KafkaTemplate<String, ScanEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //This method will run exactly once during application startup.
    // It produces a dummy event for testing or demonstration purposes.
    // In a real-world scenario, you might trigger production of events based on external actions.
    @PostConstruct
    public void produceDummyEvent() {
        ScanEvent event = new ScanEvent();
        event.setRepo("juice-shop");
        event.setOwner("kritik05");
        event.setTypes(List.of(ScanType.ALL));
//        event.setTypes(List.of(ScanType.DEPENDABOT));
        event.setParameters(List.of(ScanParameter.FAST, ScanParameter.DEEP));

        kafkaTemplate.send(ingestionTopic, event);

    }
}
