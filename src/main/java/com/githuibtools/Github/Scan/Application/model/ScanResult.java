package com.githuibtools.Github.Scan.Application.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(indexName = "scan-results")
public class ScanResult {

    @Id
    private String id;
    private String repo;
    private String owner;
    private List<ScanType> types;
    private List<ScanParameter> parameters;
    private Map<String, Object> results;
//    private JsonNode results;
//    private String rawResult;
//    private Instant timestamp = Instant.now();

    // No-arguments constructor
    public ScanResult() {
    }

    // All-arguments constructor
    public ScanResult(String id,
                      String repo,
                      String owner,
                      List<ScanType> types,
                      List<ScanParameter> parameters,
                      Map<String,Object> results) {
        this.id = id;
        this.repo = repo;
        this.owner = owner;
        this.types = types;
        this.parameters = parameters;
        this.results = results;
//        this.timestamp = timestamp;
    }


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
    public Map<String, Object> getResults() { return results; }
    public void setResults(Map<String, Object> results) { this.results = results; }
//    public JsonNode getResults() {
//        return results;
//    }
//
//    public void setResults(JsonNode results) {
//        this.results = results;
//    }
//
//    public Instant getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Instant timestamp) {
//        this.timestamp = timestamp;
//    }

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
                && Objects.equals(results, that.results);
//                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, repo, owner, types, parameters, results);
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "id='" + id + '\'' +
                ", repo='" + repo + '\'' +
                ", owner='" + owner + '\'' +
                ", types=" + types +
                ", parameters=" + parameters +
                ", results='" + results +
//                ", timestamp=" + timestamp +
                '}';
    }
}