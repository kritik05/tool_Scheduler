package com.toolScheduler.ToolSchedulerApplication.service;

import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitHubScanService {

    private final WebClient.Builder webClientBuilder;

    public GitHubScanService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String performSingleToolScan(String pat, ScanEvent event, ScanType toolType) {
        switch (toolType) {
            case CODE_SCAN:
                return fetchCodeScanningAlerts(event.getOwner(), event.getRepo(), pat);
            case DEPENDABOT:
                return fetchDependabotAlerts(event.getOwner(), event.getRepo(), pat);
            case SECRET_SCAN:
                return fetchSecretScanningAlerts(event.getOwner(), event.getRepo(), pat);
            default:
                // If we ever get "ALL" or something else, fallback to empty
                return "{}";
        }
    }

    private String fetchCodeScanningAlerts(String owner, String repo, String pat) {
        return webClientBuilder.build()
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}/code-scanning/alerts", owner, repo)
                .header("Authorization", "Bearer " + pat)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]")
                .block();
    }
    private String fetchDependabotAlerts(String owner, String repo, String pat) {
        return webClientBuilder.build()
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}/dependabot/alerts", owner, repo)
                .header("Authorization", "Bearer " + pat)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]")
                .block();
    }
    private String fetchSecretScanningAlerts(String owner, String repo, String pat) {
        return webClientBuilder.build()
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}/secret-scanning/alerts", owner, repo)
                .header("Authorization", "Bearer " + pat)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]")
                .block();
    }
}