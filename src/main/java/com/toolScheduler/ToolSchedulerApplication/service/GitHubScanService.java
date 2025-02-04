package com.toolScheduler.ToolSchedulerApplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public String performSingleToolScan(String pat, ScanEvent event, ScanType toolType) throws JsonProcessingException {
        switch (toolType) {
            case CODE_SCAN:
                return fetchScanningAlerts(event.getOwner(), event.getRepo(), pat,"code-scanning");
            case DEPENDABOT:
                return fetchScanningAlerts(event.getOwner(), event.getRepo(), pat,"dependabot");
            case SECRET_SCAN:
                return fetchScanningAlerts(event.getOwner(), event.getRepo(), pat,"secret-scanning");
            default:
                return "{}";
        }
    }


private String fetchScanningAlerts(String owner, String repo, String pat, String tool) throws JsonProcessingException {
    StringBuilder combined = new StringBuilder("[");
     int page = 1;
     int per_page=100;
    boolean gotResults = true;

    while (gotResults) {
        final int p=page;
        String pageJson = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.github.com")
                        .path("/repos/{owner}/{repo}/{tool}/alerts")
                        .queryParam("per_page",100)
                        .queryParam("page", p)
                        .build(owner, repo,tool)
                )
                .header("Authorization", "Bearer " + pat)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (pageJson == null || pageJson.equals("[]")) {
            gotResults = false;
        } else {
            combined.append(pageJson.substring(1, pageJson.length() - 1)).append(",");
            page++;
        }
    }

    if (combined.length() > 1 && combined.charAt(combined.length() - 1) == ',') {
        combined.deleteCharAt(combined.length() - 1);
    }
    combined.append("]");
    return combined.toString();
}

}