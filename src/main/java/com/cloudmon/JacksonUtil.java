package com.cloudmon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by yaleiw on 7/31/16.
 */
public class JacksonUtil {
    public static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static<T> T fromJson(String s, Class<T> tClass) throws IOException {
        return objectMapper.readValue(s, tClass);
    }

    public static<T> List<T> listFromJson(String s) throws IOException {
        return objectMapper.readValue(s, new TypeReference<List<T>>(){});
    }

    public static<T> T fromJson(String jsonString, JavaType javaType) throws IOException {
        return objectMapper.readValue(jsonString, javaType);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
