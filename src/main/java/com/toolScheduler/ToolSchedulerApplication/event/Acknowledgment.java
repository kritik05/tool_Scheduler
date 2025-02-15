package com.toolScheduler.ToolSchedulerApplication.event;

public interface Acknowledgment<T> {
    String getAcknowledgementId();
    T getPayload();
}
