package ru.at.mini.market.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import io.qameta.allure.attachment.http.HttpRequestAttachment;
import io.qameta.allure.attachment.http.HttpResponseAttachment;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrettyAllureOkHttp implements Interceptor {

    private String requestTemplatePath = "http-request.ftl";
    private String responseTemplatePath = "http-response.ftl";


    public PrettyAllureOkHttp() {
    }

    public PrettyAllureOkHttp setRequestTemplate(String templatePath) {
        this.requestTemplatePath = templatePath;
        return this;
    }

    public PrettyAllureOkHttp setResponseTemplate(String templatePath) {
        this.responseTemplatePath = templatePath;
        return this;
    }

    @SuppressWarnings("NullableProblems")
    public Response intercept(Chain chain) throws IOException {
        AttachmentProcessor<AttachmentData> processor = new DefaultAttachmentProcessor();
        Request request = chain.request();
        String requestUrl = request.url().toString();
        HttpRequestAttachment.Builder requestAttachmentBuilder = HttpRequestAttachment.Builder.create("Request", requestUrl).setMethod(request.method()).setHeaders(toMapConverter(request.headers().toMultimap()));
        RequestBody requestBody = request.body();
        if (Objects.nonNull(requestBody)) {
            requestAttachmentBuilder.setBody(readRequestBody(requestBody));
        }

        HttpRequestAttachment requestAttachment = requestAttachmentBuilder.build();
        processor.addAttachment(requestAttachment, new FreemarkerAttachmentRenderer(this.requestTemplatePath));
        Response response = chain.proceed(request);
        io.qameta.allure.attachment.http.HttpResponseAttachment.Builder responseAttachmentBuilder = io.qameta.allure.attachment.http.HttpResponseAttachment.Builder.create("Response").setResponseCode(response.code()).setHeaders(toMapConverter(response.headers().toMultimap()));
        okhttp3.Response.Builder responseBuilder = response.newBuilder();
        ResponseBody responseBody = response.body();
        if (Objects.nonNull(responseBody)) {
            byte[] bytes = responseBody.bytes();
            String prettyBody = tryGetPrettyJson(new String(bytes, StandardCharsets.UTF_8));
            responseAttachmentBuilder.setBody(prettyBody);
            responseBuilder.body(ResponseBody.create(responseBody.contentType(), bytes));
        }

        HttpResponseAttachment responseAttachment = responseAttachmentBuilder.build();
        processor.addAttachment(responseAttachment, new FreemarkerAttachmentRenderer(this.responseTemplatePath));
        return responseBuilder.build();
    }

    private String tryGetPrettyJson(String body) {
        ObjectMapper mapper = new ObjectMapper();
        String trimBody = body.trim();
        String result = body;
        if ((trimBody.startsWith("{") && trimBody.endsWith("}")) || (trimBody.startsWith("[") && trimBody.endsWith("]"))) {
            try {
                Object o = mapper.readValue(body, Object.class);
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            } catch (JsonProcessingException e) {
                return body;
            }
        }
        return result;
    }

    private static Map<String, String> toMapConverter(Map<String, List<String>> items) {
        Map<String, String> result = new HashMap<>();
        items.forEach((key, value) -> result.put(key, String.join("; ", value)));
        return result;
    }

    private static String readRequestBody(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readString(StandardCharsets.UTF_8);
    }

}
