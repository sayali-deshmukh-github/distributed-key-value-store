package com.example.dynamo_key_value.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageEngine implements StorageEngine {
    private final Map<String, List<VersionedValue>> storage = new ConcurrentHashMap<>();

    @Override
    public void put(String key, List<VersionedValue> values) {
        storage.put(key, values);
    }

    @Override
    public List<VersionedValue> get(String key) {
        return storage.getOrDefault(key, new ArrayList<>());
    }
    
}
