package com.githuibtools.Github.Scan.Application.service;

import com.githuibtools.Github.Scan.Application.model.ScanEvent;
import com.githuibtools.Github.Scan.Application.model.ScanParameter;
import com.githuibtools.Github.Scan.Application.model.ScanType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
@Service
public class GitHubScanService {

    private final WebClient.Builder webClientBuilder;

    // Manually defined constructor (replaces @RequiredArgsConstructor)
    public GitHubScanService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Perform scans by calling the appropriate GitHub endpoints.
     * Combine results into one JSON string for storage (or store them in separate fields).
     */
    public String performScan(String pat, ScanEvent event) {
        StringBuilder combinedJson = new StringBuilder();
        combinedJson.append("{");
        combinedJson.append("\"owner\": \"").append(event.getOwner()).append("\",");
        combinedJson.append("\"repo\": \"").append(event.getRepo()).append("\",");

        combinedJson.append("\"results\": {");

        // If "ALL" is present, run all three. Otherwise, selectively run them.
        if (event.getTypes().contains(ScanType.ALL) || event.getTypes().contains(ScanType.CODE_SCAN)) {
            String codeScanJson = fetchCodeScanningAlerts(event.getOwner(), event.getRepo(), pat);
            combinedJson.append("\"codeScanningAlerts\":").append(codeScanJson).append(",");
        }
        if (event.getTypes().contains(ScanType.ALL) || event.getTypes().contains(ScanType.DEPENDABOT)) {
            String dependabotJson = fetchDependabotAlerts(event.getOwner(), event.getRepo(), pat);
            combinedJson.append("\"dependabotAlerts\":").append(dependabotJson).append(",");
        }
        if (event.getTypes().contains(ScanType.ALL) || event.getTypes().contains(ScanType.SECRET_SCAN)) {
            String secretScanJson = fetchSecretScanningAlerts(event.getOwner(), event.getRepo(), pat);
            combinedJson.append("\"secretScanningAlerts\":").append(secretScanJson).append(",");
        }

        // Remove trailing comma if it exists
        if (combinedJson.charAt(combinedJson.length() - 1) == ',') {
            combinedJson.deleteCharAt(combinedJson.length() - 1);
        }
        combinedJson.append("}}");

        return combinedJson.toString();
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
