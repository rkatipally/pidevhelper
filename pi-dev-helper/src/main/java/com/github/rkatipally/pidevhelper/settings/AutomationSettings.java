package com.github.rkatipally.pidevhelper.settings;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.core.io.Resource;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class AutomationSettings {
    private List<String> users;
    private Resource testDataMappingPath;
    private Resource apiDataMappingPath;
    private String testDataMappingCollection;
    private String apiDataMappingCollection;
    private GitHub github;

    @Getter @Setter
    @ToString
    @Builder
    public static class GitHub{
        private String url;
        private String owner;
        private String repo;
        private String branch;
        private String apiDataMappingPath;
        private String testDataMappingPath;
        private String token;
    }
}
