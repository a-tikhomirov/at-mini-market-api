package ru.at.mini.market.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;

public class PrettyLogger implements HttpLoggingInterceptor.Logger {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void log(String message) {
        String trimMsg = message.trim();
        if ((trimMsg.startsWith("{") && trimMsg.endsWith("}")) || (trimMsg.startsWith("[") && trimMsg.endsWith("]"))) {
            try {
                Object o = mapper.readValue(message, Object.class);
                String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
                Platform.get().log(Platform.INFO, prettyJson, null);
            } catch (JsonProcessingException e) {
                Platform.get().log(Platform.WARN, message, e);
            }
        } else {
            Platform.get().log(Platform.INFO, message, null);
        }
    }

}
