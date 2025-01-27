package com.githuibtools.Github.Scan.Application.model;

public enum ScanType {
    ALL,          // Means run all scans
    CODE_SCAN,    // CodeQL
    DEPENDABOT,   // Dependabot
    SECRET_SCAN   // Some secret scanning
}
