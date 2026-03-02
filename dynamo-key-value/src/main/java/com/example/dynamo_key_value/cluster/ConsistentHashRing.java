package com.example.dynamo_key_value.cluster;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashRing {

    private final TreeMap<Long, Node> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashRing(List<Node> nodes, int virtualNodes) {
        this.virtualNodes = virtualNodes;
        for (Node node : nodes) {
            addNode(node);
        }
    }

    private void addNode(Node node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(node.getNodeId() + "-" + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(Node node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(node.getNodeId() + "-" + i);
            ring.remove(hash);
        }
    }

    public Node getNode(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("Ring is empty");
        }
        long hash = hash(key);
        Map.Entry<Long, Node> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));

            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash = (hash << 8) | (digest[i] & 0xFF);
            }
            return hash & 0x7FFFFFFFFFFFFFFFL; // Ensure non-negative

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Node> getReplicas(String key, int replicationFactor) {

        List<Node> replicas = new ArrayList<>();

        long hash = hash(key);
        SortedMap<Long, Node> tailMap = ring.tailMap(hash);

        Iterator<Node> iterator = tailMap.values().iterator();

        while (replicas.size() < replicationFactor) {

            if (!iterator.hasNext()) {
                iterator = ring.values().iterator(); // wrap
            }

            Node node = iterator.next();

            if (!replicas.contains(node)) {
                replicas.add(node);
            }
        }

        return replicas;
    }
    
}
