package com.github.rkatipally.pidevhelper.repository;

import java.util.List;

import com.github.rkatipally.pidevhelper.model.TestDataMapping;

public interface TestDataMappingRepository {
    List<TestDataMapping> insert(List<TestDataMapping> batchToSave, String collectionName);
    List<TestDataMapping> findAll(Class<TestDataMapping> entityClass, String collectionName);
    void dropCollection(String collectionName);
}
