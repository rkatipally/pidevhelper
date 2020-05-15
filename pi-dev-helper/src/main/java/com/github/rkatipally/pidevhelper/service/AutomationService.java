package com.github.rkatipally.pidevhelper.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rkatipally.pidevhelper.model.ApiDataMapping;
import com.github.rkatipally.pidevhelper.model.ApiDataMappingRaw;
import com.github.rkatipally.pidevhelper.repository.ApiDataMappingRepository;
import com.github.rkatipally.pidevhelper.settings.AutomationSettings;
import com.github.rkatipally.pidevhelper.settings.GitHubSettings;
import com.github.rkatipally.pidevhelper.util.AutomationUtil;
import com.github.rkatipally.pidevhelper.util.OkHttpClientUtil;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.github.rkatipally.pidevhelper.constants.AutomationConstants.EMPTY_JSON;
import static com.github.rkatipally.pidevhelper.constants.AutomationConstants.FILE_TYPE;
import static com.github.rkatipally.pidevhelper.constants.AutomationConstants.HTTP_GET;
import static com.github.rkatipally.pidevhelper.constants.AutomationConstants.HTTP_POST;
import static com.github.rkatipally.pidevhelper.constants.AutomationConstants.JSON_EXTENSION;
import static com.github.rkatipally.pidevhelper.constants.AutomationConstants.fieldsToRemove;

import com.github.rkatipally.pidevhelper.model.DataCombination;
import com.github.rkatipally.pidevhelper.model.TestDataMapping;
import com.github.rkatipally.pidevhelper.repository.TestDataMappingRepository;

@Service
@AllArgsConstructor
@Slf4j
@ToString
public class AutomationService {

    final private AutomationSettings automationSettings;
    final private TestDataMappingRepository testDataMappingRepository;
    final private ApiDataMappingRepository apiDataMappingRepository;
    final private ObjectMapper objectMapper = new ObjectMapper();

    public void loadData() {
        try {
            clearTestData();
            loadTestDataMapping();
            loadApiDataMapping();
        } catch (Exception ex) {
            log.error("Error occurred while loading test data ", ex);
        }
    }

    public String getResponseForApi(HttpServletRequest request) throws IOException {
        String url = request.getRequestURI();
        ApiDataMapping apiDataMapping = new ApiDataMapping();

        switch (request.getMethod()) {
            case HTTP_GET:
                log.info("Mocking for URL - {}", url);
                apiDataMapping = apiDataMappingRepository.findByUrl(url, automationSettings.getApiDataMappingCollection());
                break;
            case HTTP_POST:
                JSONObject requestJson = objectMapper.readValue(request.getReader(), JSONObject.class);
                AutomationUtil.removeFields(requestJson, fieldsToRemove);
                log.info("Mocking for URL - {}, with request - {}", url, requestJson.toJSONString());
                apiDataMapping = apiDataMappingRepository.findByUrlAndRequest(url, requestJson, automationSettings.getApiDataMappingCollection());

        }
        if (apiDataMapping == null) return EMPTY_JSON;
        return objectMapper.writeValueAsString(apiDataMapping.getResponse());
    }

    public void loadDataFromGitHub(GitHubSettings gitHub) throws IOException {
        if(gitHub !=null) automationSettings.setGitHubSettings(gitHub);
        GitHub github = new GitHubBuilder()
                .withConnector(new OkHttpConnector(OkHttpClientUtil.trustAllSslClient(new OkHttpClient())))
                .withEndpoint(automationSettings.getGitHubSettings().getUrl())
                .withJwtToken(automationSettings.getGitHubSettings().getToken()).build();
        GHRepository gitHubRepository = github.getRepository(automationSettings.getGitHubSettings().getRepo());
        List<GHContent> contents = gitHubRepository.getDirectoryContent(automationSettings.getGitHubSettings().getApiDataMappingPath());
        Queue<GHContent> contentsQueue = new LinkedList<>(contents);
        List<ApiDataMappingRaw> decodedList = new ArrayList<>();
        while (!contentsQueue.isEmpty()) {
            GHContent repositoryContents = contentsQueue.remove();
            if (FILE_TYPE.equals(repositoryContents.getType())) {
                if (!StringUtils.endsWithIgnoreCase(repositoryContents.getPath(), JSON_EXTENSION)) continue;
                decodedList.addAll(objectMapper.readValue(repositoryContents.read(), new TypeReference<List<ApiDataMappingRaw>>() {
                }));
            } else {
                contentsQueue.addAll(gitHubRepository.getDirectoryContent(repositoryContents.getPath()));
            }
        }
        apiDataMappingRepository.dropCollection(automationSettings.getApiDataMappingCollection());
        this.insertApiDataMapping(decodedList);
    }

    public boolean isTestUser(String userId) {
        return !StringUtils.isEmpty(userId) && automationSettings.getUsers().contains(userId.toLowerCase());
    }

    public void loadTestDataMapping() throws IOException {
        log.info("Settings- {}", automationSettings);
        List<TestDataMapping> testDataMappingList = AutomationUtil.readResource(automationSettings.getTestDataMappingPath(), TestDataMapping.class);
        log.info("TestDataMapping size is - {}", testDataMappingList.size());
        testDataMappingRepository.insert(testDataMappingList, automationSettings.getTestDataMappingCollection());
    }

    public void loadApiDataMapping() throws IOException {
        List<ApiDataMappingRaw> rawApiDataMappingList = AutomationUtil.readResource(automationSettings.getApiDataMappingPath(), ApiDataMappingRaw.class);
        this.insertApiDataMapping(rawApiDataMappingList);
    }

    public void insertApiDataMapping(List<ApiDataMappingRaw> rawApiDataMappingList) {
        List<ApiDataMapping> apiDataMappingList = new ArrayList<>();
        for (ApiDataMappingRaw rawApiDataMapping : rawApiDataMappingList) {
            for (DataCombination dataCombination : rawApiDataMapping.getCombinations()) {
                ApiDataMapping apiDataMapping = new ApiDataMapping();
                AutomationUtil.removeFields(dataCombination.getRequest(), fieldsToRemove);
                BeanUtils.copyProperties(dataCombination, apiDataMapping);
                BeanUtils.copyProperties(rawApiDataMapping, apiDataMapping);
                apiDataMapping.setUrl(rawApiDataMapping.getBaseUrl() + dataCombination.getUrlStr());
                apiDataMappingList.add(apiDataMapping);
            }
        }
        log.info("ApiDataMapping size is - {}", apiDataMappingList.size());
        apiDataMappingRepository.insert(apiDataMappingList, automationSettings.getApiDataMappingCollection());
    }

    public void clearTestData() {
        testDataMappingRepository.dropCollection(automationSettings.getTestDataMappingCollection());
        apiDataMappingRepository.dropCollection(automationSettings.getApiDataMappingCollection());
    }
}
