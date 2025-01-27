package com.githuibtools.Github.Scan.Application.model;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Document(indexName = "scan-results") // You can choose a name
public class ScanResult {

    @Id
    private String id;                       // This can be generated or assigned
    private String repo;
    private String owner;
    private List<ScanType> types;
    private List<ScanParameter> parameters;
    private String rawResult;                // Could store raw JSON or a structured object
    private Instant timestamp = Instant.now();

    // No-arguments constructor
    public ScanResult() {
    }

    // All-arguments constructor
    public ScanResult(String id,
                      String repo,
                      String owner,
                      List<ScanType> types,
                      List<ScanParameter> parameters,
                      String rawResult,
                      Instant timestamp) {
        this.id = id;
        this.repo = repo;
        this.owner = owner;
        this.types = types;
        this.parameters = parameters;
        this.rawResult = rawResult;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getRawResult() {
        return rawResult;
    }

    public void setRawResult(String rawResult) {
        this.rawResult = rawResult;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    // equals, hashCode, and toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScanResult)) return false;
        ScanResult that = (ScanResult) o;
        return Objects.equals(id, that.id)
                && Objects.equals(repo, that.repo)
                && Objects.equals(owner, that.owner)
                && Objects.equals(types, that.types)
                && Objects.equals(parameters, that.parameters)
                && Objects.equals(rawResult, that.rawResult)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, repo, owner, types, parameters, rawResult, timestamp);
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "id='" + id + '\'' +
                ", repo='" + repo + '\'' +
                ", owner='" + owner + '\'' +
                ", types=" + types +
                ", parameters=" + parameters +
                ", rawResult='" + rawResult + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}