package ru.at.mini.market.api.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import ru.at.mini.market.api.dto.Product;

import java.util.List;

public interface ProductService {

    @GET("products")
    Call<List<Product>> getProducts();

    @POST("products")
    Call<Product> createProduct(@Body Product createProductRequest);

    @PUT("products")
    Call<Product> modifyProduct(@Body Product modifyProductRequest);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") int id);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") String id);

}
