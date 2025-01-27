package com.githuibtools.Github.Scan.Application.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the credentials for a specific owner-repo combination.
 */
@Entity
@IdClass(Credential.CredentialId.class)
public class Credential {

    @Id
    @Column(nullable = false)
    private String owner;

    @Id
    @Column(nullable = false)
    private String repo;

    @Column(nullable = false)
    private String pat;

    // Default constructor
    public Credential() {
    }

    // All-args constructor
    public Credential(String owner, String repo, String pat) {
        this.owner = owner;
        this.repo = repo;
        this.pat = pat;
    }

    // Getters and setters
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPat() {
        return pat;
    }

    public void setPat(String pat) {
        this.pat = pat;
    }

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credential)) return false;
        Credential that = (Credential) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(repo, that.repo) &&
                Objects.equals(pat, that.pat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, repo, pat);
    }

    @Override
    public String toString() {
        return "Credential{" +
                "owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                ", pat='" + pat + '\'' +
                '}';
    }

    /**
     * Composite primary key class for Credential.
     */
    public static class CredentialId implements Serializable {

        private String owner;
        private String repo;

        // Default constructor
        public CredentialId() {
        }

        // All-args constructor
        public CredentialId(String owner, String repo) {
            this.owner = owner;
            this.repo = repo;
        }

        // Getters and setters
        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getRepo() {
            return repo;
        }

        public void setRepo(String repo) {
            this.repo = repo;
        }

        // equals, hashCode, and toString
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CredentialId)) return false;
            CredentialId that = (CredentialId) o;
            return Objects.equals(owner, that.owner) &&
                    Objects.equals(repo, that.repo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, repo);
        }

        @Override
        public String toString() {
            return "CredentialId{" +
                    "owner='" + owner + '\'' +
                    ", repo='" + repo + '\'' +
                    '}';
        }
    }
}
