package com.example.dynamo_key_value.service;

import java.lang.Runtime.Version;

import org.springframework.stereotype.Service;

import com.example.dynamo_key_value.cluster.Cluster;
import com.example.dynamo_key_value.config.ClusterProperties;

@Service
public class ClusterService {

    private final Cluster cluster;

    public ClusterService(ClusterProperties properties) {
        this.cluster = new Cluster(
            properties.getNodeCount(),
            properties.getReplicationFactor(),
            properties.getReadQuorum(),
            properties.getWriteQuorum()
        );
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void put(String key, String value) {
        cluster.put(key, value);
    }

    public String get(String key) {
        return cluster.get(key);
    }
    
}
