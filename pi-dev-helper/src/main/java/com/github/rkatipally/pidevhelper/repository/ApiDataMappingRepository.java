package com.github.rkatipally.pidevhelper.repository;

import org.json.simple.JSONObject;

import java.util.List;

import com.github.rkatipally.pidevhelper.model.ApiDataMapping;

public interface ApiDataMappingRepository {
    List<ApiDataMapping> insert(List<ApiDataMapping> batchToSave, String collectionName);
    List<ApiDataMapping> findAll(Class<ApiDataMapping> entityClass, String collectionName);
    void dropCollection(String collectionName);
    ApiDataMapping findByUrlAndRequest(String url, JSONObject request, String collectionName);
    ApiDataMapping findByUrl(String url, String collectionName);
}
