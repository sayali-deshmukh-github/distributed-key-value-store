package com.example.dynamo_key_value.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cluster")
public class ClusterProperties {

    private int nodeCount;
    private int replicationFactor;
    private int readQuorum;
    private int writeQuorum;



    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }
    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public int getReadQuorum() {
        return readQuorum;
    }
    public void setReadQuorum(int readQuorum) {
        this.readQuorum = readQuorum;
    }

    public int getWriteQuorum() {
        return writeQuorum;
    }
    public void setWriteQuorum(int writeQuorum) {
        this.writeQuorum = writeQuorum;
    }
}
