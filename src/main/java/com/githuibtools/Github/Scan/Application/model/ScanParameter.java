package com.githuibtools.Github.Scan.Application.model;

public enum ScanParameter {
    FAST,     // e.g. for code scanning: quick mode
    DEEP,     // e.g. for code scanning: deep analysis
    DEPENDENCIES,  // for Dependabot scans
    SECRETS        // for secret scans
}
