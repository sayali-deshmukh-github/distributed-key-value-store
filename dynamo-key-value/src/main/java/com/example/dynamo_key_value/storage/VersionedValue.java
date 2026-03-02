package com.example.dynamo_key_value.storage;

import com.example.dynamo_key_value.versioning.VectorClock;

public class VersionedValue {
    private final String value;
    private final long timestamp; // <-- add timestamp
    private final VectorClock vectorClock;

    public VersionedValue(String value, VectorClock vectorClock, long timestamp) {
        this.value = value;
        this.vectorClock = vectorClock;
        this.timestamp = timestamp;
    }

    // Factory method for convenience
    public static VersionedValue create(String value, String nodeId) {
        VectorClock clock = new VectorClock();
        clock.increment(nodeId);
        long now = System.currentTimeMillis();
        return new VersionedValue(value, clock, now);
    }

    public String getValue() {
        return value;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public long getTimestamp() {
        return timestamp;
    }
}