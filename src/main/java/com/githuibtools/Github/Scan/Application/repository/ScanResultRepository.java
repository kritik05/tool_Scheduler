package com.githuibtools.Github.Scan.Application.repository;


import com.githuibtools.Github.Scan.Application.model.ScanResult;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanResultRepository extends ElasticsearchRepository<ScanResult, String> {
}
