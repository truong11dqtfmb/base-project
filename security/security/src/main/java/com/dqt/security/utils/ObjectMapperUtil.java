package com.dqt.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream()
                .map(element -> objectMapper.convertValue(element, targetClass))
                .collect(Collectors.toList());
    }

    public <T> T mapObject(Object src, Class<T> des) {
        return objectMapper.convertValue(src, des);
    }

    public String objectToJson(Object data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(data);
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public String parserJson(Object data) {
        try {
            ObjectMapper mapper = objectMapper();
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            return "";
        }
    }

    public <T> T jsonToObject(String jsonData, Class<T> classOutput) throws IOException {
        ObjectMapper mapper = objectMapper();
        return mapper.readValue(jsonData, classOutput);
    }

    public <T> T readValue(String string, Class<T> classOutput) throws IOException {
        return objectMapper.readValue(string, classOutput);
    }
}

