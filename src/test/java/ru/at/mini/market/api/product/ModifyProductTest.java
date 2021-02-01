package ru.at.mini.market.api.product;

import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;
import ru.at.mini.market.api.base.Base;
import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.dto.Product;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.at.mini.market.api.utils.PropertyLoader.loadProperty;

@Feature(value = "Product PUT")
@DisplayName("Product PUT")
public class ModifyProductTest extends Base {

    Integer productId;

    @BeforeEach
    void createNewProduct() {
        Response<Product> createResponse = createProduct();
        assertThat(createResponse.body()).isNotNull();
        productId = createResponse.body().getId();
    }


    @DisplayName("Product PUT - positive")
    @SneakyThrows
    @ParameterizedTest(name = "Product PUT - positive: {0}")
    @MethodSource("getValidProducts")
    public void productPutPositiveTest(Product product) {
        product.setId(productId);
        Response<Product> response = productService.modifyProduct(product).execute();
        assertThat(response)
                .satisfies(r -> assertThat(r.code()).isEqualTo(200))
                .satisfies(r -> assertThat(r.body()).isNotNull()
                        .satisfies(body -> assertThat(body).isEqualTo(product))
                );
    }

    private static Stream<Arguments> getInvalidProducts() {
        return Stream.of(
                Arguments.of(new Product()
                        .withId(faker.number().numberBetween(Integer.MIN_VALUE, -1))
                        .withTitle(faker.lorem().word())
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title), 400),
                Arguments.of(new Product()
                        .withId(null)
                        .withTitle(faker.lorem().word())
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.ELECTRONICS.title), 400),
                Arguments.of(new Product()
                        .withId(0)
                        .withTitle(faker.lorem().word())
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(faker.lorem().word()), 500),
                Arguments.of(new Product()
                        .withId(0)
                        .withTitle(faker.lorem().fixedString(512))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.ELECTRONICS.title), 500),
                Arguments.of(new Product()
                        .withId(0)
                        .withTitle(loadProperty("text.special.chars"))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title), 400),
                Arguments.of(new Product()
                        .withId(0)
                        .withTitle("")
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title), 400),
                Arguments.of(new Product()
                        .withId(0)
                        .withTitle(null)
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.ELECTRONICS.title), 400),
                Arguments.of(new Product()
                        .withId(0)
                        .withTitle(faker.lorem().word())
                        .withPrice(faker.number().numberBetween(Integer.MIN_VALUE, -1))
                        .withCategoryTitle(Category.ELECTRONICS.title), 400),
                Arguments.of(new Product()
                        .withId(0)
                        .withTitle(faker.lorem().word())
                        .withPrice(0)
                        .withCategoryTitle(Category.ELECTRONICS.title), 400)
        );
    }

    /**
     * Баг: Неверный код при модификации продукта с параметрами:
     * title: пустое значение/null/специальные символы
     * price: <=0
     * ОР: 400; ФР: 200
     */
    @Issue("wrong.status.code")
    @DisplayName("Product PUT - negative")
    @SneakyThrows
    @ParameterizedTest(name = "Product PUT - negative: {0}; expectedCode={1}")
    @MethodSource("getInvalidProducts")
    public void productPutNegativeTest(Product product, int expectedCode) {
        if (product.getId() != null && product.getId() == 0) {
            product.setId(productId);
        }
        Response<Product> response = productService.modifyProduct(product).execute();
        assertThat(response.code()).isEqualTo(expectedCode);
    }

    @AfterEach
    void deleteProduct() {
        try {
            productService.deleteProduct(productId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
