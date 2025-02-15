package com.toolScheduler.ToolSchedulerApplication.event;

public interface Event <T>{
    String getType();
    T getPayload();
    String getEventId();
}
