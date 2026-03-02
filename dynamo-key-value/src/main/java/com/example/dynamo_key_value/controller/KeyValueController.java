package com.example.dynamo_key_value.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamo_key_value.dto.GetResponse;
import com.example.dynamo_key_value.dto.PutRequest;
import com.example.dynamo_key_value.service.ClusterService;

@RestController
@RequestMapping("/kv")
public class KeyValueController {
    private final ClusterService clusterService;

    public KeyValueController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @PostMapping
    public void put(@RequestBody PutRequest request) {
        clusterService.put(request.getKey(), request.getValue());
    }

    @GetMapping("/{key}")
    public GetResponse get(@PathVariable String key) {
        String value = clusterService.get(key);
        return new GetResponse(value);
    }
    
}
