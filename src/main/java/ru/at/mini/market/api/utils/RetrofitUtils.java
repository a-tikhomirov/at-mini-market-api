package ru.at.mini.market.api.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static ru.at.mini.market.api.utils.PropertyLoader.loadProperty;

public class RetrofitUtils {

    private final static HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new PrettyLogger());
    private final static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit getRetrofit() {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient
                .addInterceptor(logging)
                .addInterceptor(new PrettyAllureOkHttp()
                        .setRequestTemplate("http-request.ftl")
                        .setResponseTemplate("http-response.ftl"));
        return new Retrofit.Builder()
                .baseUrl(loadProperty("base.url"))
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

}
