package com.github.rkatipally.pidevhelper.model;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class DataCombination {
    private String id;
    private String urlStr;
    private JSONObject request;
    private JSONObject response;
}
