package ru.at.mini.market.api.product;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import ru.at.mini.market.api.base.Base;
import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.dto.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Feature(value = "Product GET: all products")
@DisplayName("Product GET: all products")
public class GetProductsTest extends Base {

    List<Product> products = new ArrayList<>();

    @BeforeEach
    void addProducts() {
        products.add(new Product()
                .withTitle(faker.food().ingredient())
                .withPrice(faker.number().numberBetween(1, 100))
                .withCategoryTitle(Category.FOOD.title));
        products.add(new Product()
                .withTitle(faker.app().name())
                .withPrice(faker.number().numberBetween(100, 1000))
                .withCategoryTitle(Category.ELECTRONICS.title));
        products.forEach(product -> {
            try {
                Response<Product> response = productService.createProduct(product).execute();
                assertThat(response.body()).isNotNull();
                product.setId(response.body().getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
        products.forEach(product -> {
            try {
                productService.deleteProduct(product.getId()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
