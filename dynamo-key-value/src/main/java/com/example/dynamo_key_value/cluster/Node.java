package com.example.dynamo_key_value.cluster;

import java.util.ArrayList;
import java.util.List;

import com.example.dynamo_key_value.storage.InMemoryStorageEngine;
import com.example.dynamo_key_value.storage.StorageEngine;
import com.example.dynamo_key_value.storage.VersionedValue;
import com.example.dynamo_key_value.versioning.VectorClock;

public class Node {
    private final String nodeId;
    private final StorageEngine storage;
    private volatile boolean isUp = true;

    public Node(String nodeId) {
        this.nodeId = nodeId;
        this.storage = new InMemoryStorageEngine();
    }

    public void put(String key, String value) {
        checkIfUp();
        List<VersionedValue> existing = storage.get(key);

        VectorClock newClock = new VectorClock();

        if (!existing.isEmpty()) {
            for (VersionedValue vv : existing) {
                newClock = VectorClock.merge(newClock, vv.getVectorClock());
            }
        }

        newClock.increment(nodeId);

        VersionedValue newValue = VersionedValue.create(value, nodeId);

        storage.put(key, List.of(newValue));
    }

    public void put(String key, VersionedValue value) {
        // Get the existing list from storage
        List<VersionedValue> existing = storage.get(key);

        // If no list exists yet, create a new one
        if (existing == null) {
            existing = new ArrayList<>();
        }

        // Add the new VersionedValue
        existing.add(value);

        // Store the updated list back in storage
        storage.put(key, existing);
    }  

    public List<VersionedValue> get(String key) {
        checkIfUp();
        return storage.get(key);
    }

    private void checkIfUp() {
        if (!isUp) {
            throw new IllegalStateException("Node " + nodeId + " is DOWN");
        }
    }

    public void setDown() {
        isUp = false;
    }

    public void setUp() {
        isUp = true;
    }

    public String getNodeId() {
        return nodeId;
    }
}
