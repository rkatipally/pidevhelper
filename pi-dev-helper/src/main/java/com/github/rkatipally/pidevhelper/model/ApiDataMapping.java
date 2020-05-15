package com.github.rkatipally.pidevhelper.model;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "database_sequences")
public class ApiDataMapping {
    @Id
    private String uniqueId;
    private String name;
    private String url;
    private String id;
    private JSONObject request;
    private JSONObject response;

}
