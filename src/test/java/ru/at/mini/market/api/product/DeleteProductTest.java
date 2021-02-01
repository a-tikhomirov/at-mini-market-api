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

@Feature(value = "Product DELETE")
@DisplayName("Product DELETE")
public class DeleteProductTest extends Base {

    @DisplayName("Product DELETE - positive")
    @SneakyThrows
    @Test
    void productDeletePositiveTest() {
        Response<Product> createResponse = createProduct();
        assertThat(createResponse.body()).isNotNull();
        Response<ResponseBody> deleteResponse =  deleteProduct(createResponse.body().getId());
        assertThat(deleteResponse.code()).isEqualTo(200);
    }

    /**
     * Баг: Неверный код при запросе удаления уже удаленного продукта.
     * ФР: 500; ОР: 404
     */
    @Issue("wrong.status.code")
    @DisplayName("Product DELETE - negative: wrong id")
    @SneakyThrows
    @Test
    public void productDeleteWrongIdNegativeTest() {
        Response<Product> createResponse = createProduct();
        assertThat(createResponse.body()).isNotNull();
        Response<ResponseBody> deleteResponse =  deleteProduct(createResponse.body().getId());
        assertThat(deleteResponse.code()).isEqualTo(200);
        deleteResponse =  productService.deleteProduct(createResponse.body().getId()).execute();
        assertThat(deleteResponse.code()).isEqualTo(404);
    }

    @DisplayName("Product DELETE - negative: illegal type id")
    @SneakyThrows
    @Test
    public void dproductDeleteIllegalTypeIdNegativeTest() {
        Response<ResponseBody> deleteResponse = productService.deleteProduct(faker.lorem().fixedString(1)).execute();
        assertThat(deleteResponse.code()).isEqualTo(400);
    }

}
