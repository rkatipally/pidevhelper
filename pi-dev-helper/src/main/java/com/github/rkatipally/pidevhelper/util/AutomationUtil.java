package com.github.rkatipally.pidevhelper.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.rkatipally.pidevhelper.constants.AutomationConstants.JSON_EXTENSION;

@Slf4j
public class AutomationUtil {

    public static <T> List<T> readResource(Resource resource, Class<T> resourceType) throws IOException {
        log.info("Path is - {}", resource.getURI().getPath());
        Path configFilePath = Paths.get(resource.getURI());
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> data = Files.walk(configFilePath)
                .filter(s -> s.toString().endsWith(JSON_EXTENSION))
                .map(Path::toFile)
                .peek(s -> log.info(s.getAbsolutePath()))
                .map(file -> {
                    try {
                        JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, resourceType);
                        return (ArrayList<T>) objectMapper.readValue(file, type);
                    } catch (IOException e) {
                        log.error("Error while deserializing test file : {} ", file.getAbsoluteFile());
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return data;
    }

    public static JSONObject removeFields(JSONObject jsonObject, List<String> fields){
        fields.forEach(jsonObject::remove);
        return jsonObject;
    }

}
