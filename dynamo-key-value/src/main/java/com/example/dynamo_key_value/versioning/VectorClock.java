package com.example.dynamo_key_value.versioning;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class VectorClock {
    private final Map<String, Integer> versions = new HashMap<>();

    public VectorClock() {}

    public VectorClock(Map<String, Integer> initialVersions) {
        this.versions.putAll(initialVersions);
    }

    public Map<String, Integer> getVersions() {
        return versions;
    }

    public void increment(String nodeId) {
        versions.put(nodeId, versions.getOrDefault(nodeId, 0) + 1);
    }

    public static VectorClock merge(VectorClock a, VectorClock b) {
        VectorClock merged = new VectorClock();
        Set<String> allNodes = a.getVersions().keySet();
        allNodes.addAll(b.getVersions().keySet());
        for (String node : allNodes) {
            int versionA = a.versions.getOrDefault(node, 0);
            int versionB = b.versions.getOrDefault(node, 0);
            merged.versions.put(node, Math.max(versionA, versionB));
        }
        return merged;
    }

    public Comparison compare(VectorClock other) {
        boolean greater = false;
        boolean less = false;

        Set<String> allNodes = versions.keySet();
        allNodes.addAll(other.versions.keySet());

        for (String node : allNodes) {
            int v1 = versions.getOrDefault(node, 0);
            int v2 = other.versions.getOrDefault(node, 0);
            if (v1 > v2) {
                greater = true;
            } 
            if (v1 < v2) {
                less = true;
            }
        }

        if (greater && !less) {
            return Comparison.AFTER;
        } else if (!greater && less) {
            return Comparison.BEFORE;
        } else if (!greater && !less) {
            return Comparison.EQUAL;
        } 
        return Comparison.CONCURRENT;
    }

    public enum Comparison {
        BEFORE, AFTER, EQUAL, CONCURRENT
    }
}
