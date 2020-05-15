package com.github.rkatipally.pidevhelper.repository;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.github.rkatipally.pidevhelper.model.ApiDataMapping;


@Repository
@AllArgsConstructor
public class ApiDataMappingRepositoryImpl implements ApiDataMappingRepository {

    final private MongoTemplate template;

    @Override
    public List<ApiDataMapping> insert(List<ApiDataMapping> batchToSave, String collectionName) {
        return (List<ApiDataMapping>) template.insert(batchToSave, collectionName);
    }

    @Override
    public List<ApiDataMapping> findAll(Class<ApiDataMapping> entityClass, String collectionName) {
        return template.findAll(entityClass, collectionName);
    }

    @Override
    public void dropCollection(String collectionName) {
        if (template.getCollectionNames().contains(collectionName)) {
            template.dropCollection(collectionName);
        }
    }

    @Override
    public ApiDataMapping findByUrlAndRequest(String url, JSONObject request, String collectionName) {
        Query query = new Query().addCriteria(Criteria.where("url").is(url))
                .addCriteria(Criteria.where("request").is(request));
        return template.findOne(query, ApiDataMapping.class, collectionName);
    }
    @Override
    public ApiDataMapping findByUrl(String url, String collectionName) {
        Query query = new Query().addCriteria(Criteria.where("url").is(url));
        return template.findOne(query, ApiDataMapping.class, collectionName);
    }
}
