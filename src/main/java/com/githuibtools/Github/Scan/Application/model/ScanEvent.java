package com.githuibtools.Github.Scan.Application.model;

import java.util.List;

//lombok dependencies
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class ScanEvent {
    private String repo;
    private String owner;
    private List<ScanType> types;          // Might contain multiple types if combination
    private List<ScanParameter> parameters;

    public ScanEvent() {
    }

    // All-arguments constructor
    public ScanEvent(String repo, String owner, List<ScanType> types, List<ScanParameter> parameters) {
        this.repo = repo;
        this.owner = owner;
        this.types = types;
        this.parameters = parameters;
    }

    // Getters and setters
    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<ScanType> getTypes() {
        return types;
    }

    public void setTypes(List<ScanType> types) {
        this.types = types;
    }

    public List<ScanParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ScanParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScanEvent)) return false;

        ScanEvent scanEvent = (ScanEvent) o;
        if (repo != null ? !repo.equals(scanEvent.repo) : scanEvent.repo != null) return false;
        if (owner != null ? !owner.equals(scanEvent.owner) : scanEvent.owner != null) return false;
        if (types != null ? !types.equals(scanEvent.types) : scanEvent.types != null) return false;
        return parameters != null ? parameters.equals(scanEvent.parameters) : scanEvent.parameters == null;
    }

    @Override
    public int hashCode() {
        int result = repo != null ? repo.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (types != null ? types.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    // toString for debugging/logging
    @Override
    public String toString() {
        return "ScanEvent{" +
                "repo='" + repo + '\'' +
                ", owner='" + owner + '\'' +
                ", types=" + types +
                ", parameters=" + parameters +
                '}';
    }

}
