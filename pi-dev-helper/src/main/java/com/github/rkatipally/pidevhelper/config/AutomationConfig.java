package com.github.rkatipally.pidevhelper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

import com.github.rkatipally.pidevhelper.interceptor.AutomationInterceptor;
import com.github.rkatipally.pidevhelper.settings.AutomationSettings;

@Configuration
public class AutomationConfig implements WebMvcConfigurer {

    @Bean
    public AutomationInterceptor automationInterceptor() {
        return new AutomationInterceptor();
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(automationInterceptor());
    }

    @Bean
    public AutomationSettings automationSettings() {
        Resource testDataMapping = new ClassPathResource("automation/mappings");
        Resource apiDataMapping = new ClassPathResource("automation/data");
        return AutomationSettings.builder()
                .users(Arrays.asList("atp3rxk", "XAA$123"))
                .testDataMappingPath(testDataMapping)
                .apiDataMappingPath(apiDataMapping)
                .testDataMappingCollection("testDataMapping")
                .apiDataMappingCollection("apiDataMapping")
                .gitHubSettings(null)
                .build();
    }
}
