package ru.at.mini.market.api.product;

import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import ru.at.mini.market.api.base.Base;
import ru.at.mini.market.api.dto.Product;

import static org.assertj.core.api.Assertions.assertThat;

@Feature(value = "Product GET: by id")
@DisplayName("Product GET: by id")
public class GetProductByIdTest extends Base {

    @DisplayName("Product GET: by id - positive")
    @SneakyThrows
    @Test
    void productGetByIdPositiveTest() {
        int productId = dbCreateProduct();
        Product product = dbGetProductById(productId);
        Response<Product> getResponse = productService.getProductById(productId).execute();
        assertThat(getResponse)
                .satisfies(r -> assertThat(r.code()).isEqualTo(200))
                .satisfies(r -> assertThat(r.body()).isNotNull()
                        .satisfies(body -> assertThat(body).isEqualTo(product))
                );
        dbDeleteProduct(productId);
    }

    @DisplayName("Product GET: by id - negative: wrong id")
    @SneakyThrows
    @Test
    @Issue("wrong.status.code")
    public void productGetByIdWrongIdNegativeTest() {
        int productId = dbCreateProduct();
        dbDeleteProduct(productId);
        Response<Product> getResponse =  productService.getProductById(productId).execute();
        assertThat(getResponse.code()).isEqualTo(404);
    }

    @DisplayName("Product GET: by id - negative: illegal type id")
    @SneakyThrows
    @Test
    public void productGetByIdIllegalTypeIdNegativeTest() {
        Response<ResponseBody> deleteResponse = productService.deleteProduct(faker.lorem().fixedString(1)).execute();
        assertThat(deleteResponse.code()).isEqualTo(400);
    }

}
