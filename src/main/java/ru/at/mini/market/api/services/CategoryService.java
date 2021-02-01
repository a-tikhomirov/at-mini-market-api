package ru.at.mini.market.api.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.at.mini.market.api.dto.GetCategoryResponse;

public interface CategoryService {

    @GET("categories/{id}")
    Call<GetCategoryResponse> getCategory(@Path("id") int id);

    @GET("categories/{id}")
    Call<GetCategoryResponse> getCategory(@Path("id") String id);
}
