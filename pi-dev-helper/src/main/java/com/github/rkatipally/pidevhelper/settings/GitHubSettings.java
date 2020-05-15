package com.github.rkatipally.pidevhelper.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubSettings {
    private String url;
    private String owner;
    private String repo;
    private String branch;
    private String apiDataMappingPath;
    private String testDataMappingPath;
    private String token;
}
