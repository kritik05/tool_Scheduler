package com.githuibtools.Github.Scan.Application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.githuibtools.Github.Scan.Application.model.ScanEvent;
import com.githuibtools.Github.Scan.Application.model.ScanType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
@Service
public class GitHubScanService {

    private final WebClient.Builder webClientBuilder;

    public GitHubScanService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public JsonNode performScan(String pat, ScanEvent event) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("owner", event.getOwner());
            root.put("repo", event.getRepo());

            ObjectNode resultsNode = objectMapper.createObjectNode();

            if (event.getTypes().contains(ScanType.ALL) || event.getTypes().contains(ScanType.CODE_SCAN)) {
                String codeScanJson = fetchCodeScanningAlerts(event.getOwner(), event.getRepo(), pat);
                JsonNode codeScanNode = objectMapper.readTree(codeScanJson);
                resultsNode.set("codeScanningAlerts", codeScanNode);
            }

            if (event.getTypes().contains(ScanType.ALL) || event.getTypes().contains(ScanType.DEPENDABOT)) {
                String dependabotJson = fetchDependabotAlerts(event.getOwner(), event.getRepo(), pat);
                JsonNode dependabotNode = objectMapper.readTree(dependabotJson);
                resultsNode.set("dependabotAlerts", dependabotNode);
            }

            if (event.getTypes().contains(ScanType.ALL) || event.getTypes().contains(ScanType.SECRET_SCAN)) {
                String secretScanJson = fetchSecretScanningAlerts(event.getOwner(), event.getRepo(), pat);
                JsonNode secretScanNode = objectMapper.readTree(secretScanJson);
                resultsNode.set("secretScanningAlerts", secretScanNode);
            }

            root.set("results", resultsNode);
            return root;

        } catch (Exception e) {
            return objectMapper.createObjectNode().put("error", "Scan failed");
        }
    }
    private String fetchCodeScanningAlerts(String owner, String repo, String pat) {
        // GET /repos/{owner}/{repo}/code-scanning/alerts
        return webClientBuilder.build()
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}/code-scanning/alerts", owner, repo)
                .header("Authorization", "Bearer " + pat)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]") // fallback if error
                .block();
    }

    private String fetchDependabotAlerts(String owner, String repo, String pat) {
        // GET /repos/{owner}/{repo}/dependabot/alerts
        return webClientBuilder.build()
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}/dependabot/alerts", owner, repo)
                .header("Authorization", "Bearer " + pat)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]")
                .block();
    }

    private String fetchSecretScanningAlerts(String owner, String repo, String pat) {
        // GET /repos/{owner}/{repo}/secret-scanning/alerts
        return webClientBuilder.build()
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}/secret-scanning/alerts", owner, repo)
                .header("Authorization", "Bearer " + pat)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]")
                .block();
    }
}
