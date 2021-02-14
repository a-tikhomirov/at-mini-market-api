package ru.at.mini.market.api.product;

import io.qameta.allure.Feature;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import ru.at.mini.market.api.base.Base;
import ru.at.mini.market.api.db.model.ProductsExample;
import ru.at.mini.market.api.dto.Product;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.at.mini.market.api.dto.DbRetrofitMapper.getProductsListFromDbProducts;

@Feature(value = "Product GET: all products")
@DisplayName("Product GET: all products")
public class GetProductsTest extends Base {

    List<Product> products = new ArrayList<>();
    List<Integer> toDelete = new ArrayList<>();

    @BeforeEach
    void addProducts() {
        getValidProducts().forEach(product -> toDelete.add(dbCreateProduct(product)));
        products = getProductsListFromDbProducts(productsMapper.selectByExample(new ProductsExample()));
    }

    @DisplayName("Product GET: all products")
    @SneakyThrows
    @Test
    void productGetTest() {
        Response<List<Product>> response = productService.getProducts().execute();
        assertThat(response)
                .satisfies(r -> assertThat(r.code()).isEqualTo(200))
                .satisfies(r -> assertThat(r.body()).isNotNull()
                        .satisfies(body -> body.forEach(product ->
                                assertThat(product)
                                        .satisfies(p -> assertThat(p.getId()).isInstanceOf(Integer.class).isGreaterThan(0))
                                        .satisfies(p -> assertThat(p.getTitle()).isNotNull())
                                        .satisfies(p -> assertThat(p.getPrice()).isInstanceOf(Integer.class))
                                        .satisfies(p -> assertThat(p.getCategoryTitle()).isNotNull()))
                        )
                );
        products.forEach(product -> assertThat(response.body()).contains(product));
    }

    @AfterEach
    void deleteProducts() {
        toDelete.forEach(this::dbDeleteProduct);
    }

}
