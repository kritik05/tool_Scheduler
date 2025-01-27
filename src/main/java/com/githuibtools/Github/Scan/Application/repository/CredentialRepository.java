package com.githuibtools.Github.Scan.Application.repository;

import com.githuibtools.Github.Scan.Application.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Credential.CredentialId> {

    Credential findByOwnerAndRepo(String owner, String repo);
}