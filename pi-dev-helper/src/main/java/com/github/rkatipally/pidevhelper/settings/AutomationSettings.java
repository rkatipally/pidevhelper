package com.github.rkatipally.pidevhelper.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.kohsuke.github.GitHub;
import org.springframework.core.io.Resource;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationSettings {
    private List<String> users;
    private Resource testDataMappingPath;
    private Resource apiDataMappingPath;
    private String testDataMappingCollection;
    private String apiDataMappingCollection;
    private GitHubSettings gitHubSettings;
}
