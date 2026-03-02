package com.example.dynamo_key_value.storage;

import java.util.List;


public interface StorageEngine {
    void put(String key, List<VersionedValue> value);
    List<VersionedValue> get(String key);
    
}
