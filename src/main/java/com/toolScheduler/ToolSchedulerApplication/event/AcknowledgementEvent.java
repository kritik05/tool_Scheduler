package com.toolScheduler.ToolSchedulerApplication.event;

import com.toolScheduler.ToolSchedulerApplication.model.AcknowledgementPayload;

import java.util.UUID;

public class AcknowledgementEvent implements Acknowledgment<AcknowledgementPayload> {

    private String acknowledgementId;
    private AcknowledgementPayload payload;

    public AcknowledgementEvent() {
        this.acknowledgementId = UUID.randomUUID().toString();
    }

    public AcknowledgementEvent(String acknowledgementId, AcknowledgementPayload payload) {
        this.acknowledgementId = (acknowledgementId == null || acknowledgementId.isEmpty())
                ? UUID.randomUUID().toString()
                : acknowledgementId;

        this.payload = payload;
    }

    @Override
    public String getAcknowledgementId() {
        return acknowledgementId;
    }

    @Override
    public AcknowledgementPayload getPayload() {
        return payload;
    }

    public void setAcknowledgementId(String acknowledgementId) {
        this.acknowledgementId = acknowledgementId;
    }

    public void setPayload(AcknowledgementPayload payload) {
        this.payload = payload;
    }
}