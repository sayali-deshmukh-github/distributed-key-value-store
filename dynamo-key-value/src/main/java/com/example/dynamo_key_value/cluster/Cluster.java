package com.example.dynamo_key_value.cluster;

import java.util.ArrayList;
import java.util.List;

import com.example.dynamo_key_value.storage.VersionedValue;
import com.example.dynamo_key_value.versioning.VectorClock;

public class Cluster {
    private final List<Node> nodes = new ArrayList<>();
    private final ConsistentHashRing ring;
    private final int replicationFactor;
    private final int writeQuorum;
    private final int readQuorum;

    public Cluster(int nodeCount, int replicationFactor, int readQuorum, int writeQuorum) {

        this.replicationFactor = replicationFactor;
        this.readQuorum = readQuorum;
        this.writeQuorum = writeQuorum;

        for (int i = 0; i < nodeCount; i++) {
            nodes.add(new Node("Node-" + i));
        }
        this.ring = new ConsistentHashRing(nodes, 100);

    }

    public void put(String key, String value) {
        List<Node> replicas = ring.getReplicas(key, replicationFactor);
        int success = 0;
        for (Node node : replicas) {
            try {
                VersionedValue versionedValue = VersionedValue.create(value, node.getNodeId());
                node.put(key, versionedValue);
                success++;
            } catch (Exception e) {
                // Log failure
            }
        }
        if (success < writeQuorum) {
            throw new RuntimeException("Write quorum not met");
        }
    }

public String get(String key) {
    // Get the list of replicas responsible for this key
    List<Node> replicas = ring.getReplicas(key, replicationFactor);
    int success = 0;
    List<VersionedValue> allValues = new ArrayList<>();

    // Collect all values from replicas
    for (Node node : replicas) {
        try {
            List<VersionedValue> values = node.get(key);
            if (values != null && !values.isEmpty()) {
                success++;
                allValues.addAll(values);
            }
        } catch (Exception e) {
            // Log failure or ignore for now
        }
    }

    if (success < readQuorum) {
        throw new RuntimeException("Read quorum not met");
    }

    if (allValues.isEmpty()) {
        return null; // Key not found
    }

    // Start resolving versions using vector clocks
    VersionedValue latest = allValues.get(0);

    for (int i = 1; i < allValues.size(); i++) {
        VersionedValue current = allValues.get(i);
        VectorClock.Comparison cmp = current.getVectorClock().compare(latest.getVectorClock());

        switch (cmp) {
            case AFTER:
                // current version causally comes after latest
                latest = current;
                break;
            case CONCURRENT:
                // concurrent writes — pick one using timestamp as tiebreaker if available
                if (current.getTimestamp() > latest.getTimestamp()) {
                    latest = current;
                }
                break;
            case BEFORE:
            case EQUAL:
                // do nothing — latest already causally after or equal
                break;
        }
    }

    return latest.getValue();
}

    public List<VersionedValue> getAll(String key) {
        List<Node> replicas = ring.getReplicas(key, replicationFactor);
        int success = 0;
        List<VersionedValue> collected = new ArrayList<>();
        for (Node node : replicas) {
            try {
                List<VersionedValue> values = node.get(key);
                if (values != null && !values.isEmpty()) {
                    collected.addAll(values);
                    success++;
                }
            } catch (Exception e) {
                // Log failure
            }
        }
        if (success < readQuorum) {
            throw new RuntimeException("Read quorum not met");
        }
        return resolveConflicts(collected);
    }

    private List<VersionedValue> resolveConflicts(List<VersionedValue> versions) {
        List<VersionedValue> result = new ArrayList<>();

        for (VersionedValue candidate : versions) {
            boolean dominated = false;

            for (VersionedValue other : versions) {
                if (candidate == other) continue;

                VectorClock.Comparison cmp = candidate.getVectorClock().compare(other.getVectorClock());
                if (cmp == VectorClock.Comparison.BEFORE) {
                    dominated = true;
                    break;
                }
            }

            if (!dominated) {
                result.add(candidate);
            }
        }
        return result;
    }

    private Node routeToNode(String key) {
        int index = Math.abs(key.hashCode()) % nodes.size();
        return nodes.get(index);
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
