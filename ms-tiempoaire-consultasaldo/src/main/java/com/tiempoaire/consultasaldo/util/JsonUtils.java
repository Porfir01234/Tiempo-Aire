package com.tiempoaire.consultasaldo.util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode parseJson(String jsonStr) throws Exception {
        return mapper.readTree(jsonStr);
    }
}
