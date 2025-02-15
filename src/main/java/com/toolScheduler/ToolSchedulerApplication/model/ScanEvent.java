package com.toolScheduler.ToolSchedulerApplication.model;


import java.util.List;

public class ScanEvent {
    private Integer tenantId; // new, required
    private String types;


    public ScanEvent(Integer tenantId, String types) {
        this.tenantId = tenantId;
        this.types = types;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public ScanEvent() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScanEvent)) return false;
        ScanEvent scanEvent = (ScanEvent) o;
        return (tenantId != null ? tenantId.equals(scanEvent.tenantId) : scanEvent.tenantId == null) &&
                (types != null ? types.equals(scanEvent.types) : scanEvent.types == null);
    }

    @Override
    public int hashCode() {
        int result = (tenantId != null ? tenantId.hashCode() : 0);
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScanEvent{" +
                "tenantId=" + tenantId +
                ", types=" + types +
                '}';
    }
    }
