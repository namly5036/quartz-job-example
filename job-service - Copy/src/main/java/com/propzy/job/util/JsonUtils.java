package com.propzy.job.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {
    private static final ObjectMapper mapper;
    private static final ModelMapper modelMapper;

    static {
        mapper = new ObjectMapper();
        modelMapper = new ModelMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> Optional<T> objToObj(Object object, Class<T> type) {
        try {
            T map = modelMapper.map(object, type);
            return Optional.ofNullable(map);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> jsonToObj(String object, Class<T> type) {
        try {
            return Optional.ofNullable(mapper.readValue(object, type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> String objectToJson(T value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> Optional<T> jsonToObj(String object, TypeReference<T> typeReference) {
        try {
            return Optional.ofNullable(mapper.readValue(object, typeReference));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<String> objToString(Object o) {
        try {
            return Optional.ofNullable(mapper.writeValueAsString(o));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> mapToObj(Map<String, String> map, Class<T> type) {
        return Optional.ofNullable(mapper.convertValue(map, type));
    }

    public static <T> T convertMapToObject(Map<?, ?> map, Class<T> clazz) {
        return mapper.convertValue(map, clazz);
    }

	public static <T> List<T> convertListMapToListObject(List<Map<String, Object>> map, Class<T> clazz) {
		return map.stream().map(p -> convertMapToObject(p, clazz)).collect(Collectors.toList());
	}
}
