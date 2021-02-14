package ru.at.mini.market.api.product;

import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;
import ru.at.mini.market.api.base.Base;
import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.dto.Product;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.at.mini.market.api.utils.PropertyLoader.loadProperty;

@Feature(value = "Product POST")
@DisplayName("Product POST")
public class CreateProductTest extends Base {

    Integer idToDelete;

    @DisplayName("Product POST - positive")
    @SneakyThrows
    @ParameterizedTest(name = "Product POST - positive: {0}")
    @MethodSource("getValidProducts")
    public void productPostPositiveTest(Product product) {
        Response<Product> response = productService.createProduct(product).execute();
        assertThat(response)
                .satisfies(r -> assertThat(r.code()).isEqualTo(201))
                .satisfies(r -> assertThat(r.body()).isNotNull()
                        .satisfies(body -> assertThat(body.getId()).isInstanceOf(Integer.class).isGreaterThan(0))
                        .satisfies(body -> assertThat(body.getTitle()).isEqualTo(product.getTitle()))
                        .satisfies(body -> assertThat(body.getPrice()).isEqualTo(product.getPrice()))
                        .satisfies(body -> assertThat(body.getCategoryTitle()).isEqualTo(product.getCategoryTitle()))
                );
        if (response.body() != null) {
            idToDelete = response.body().getId();
            Product productFromDb = dbGetProductById(idToDelete);
            assertThat(response.body()).isEqualTo(productFromDb);
        } else idToDelete = null;
    }

    private static Stream<Arguments> getInvalidProducts() {
        return Stream.of(
                Arguments.of(new Product()
                        .withId(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withTitle(faker.lorem().word())
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title), 400),
                Arguments.of(new Product()
                        .withTitle(faker.lorem().word())
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(faker.lorem().word()), 500),
                Arguments.of(new Product()
                        .withTitle(faker.lorem().fixedString(512))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.ELECTRONIC.title), 500),
                Arguments.of(new Product()
                        .withTitle(loadProperty("text.special.chars"))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title), 400),
                Arguments.of(new Product()
                        .withTitle("")
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title), 400),
                Arguments.of(new Product()
                        .withTitle(null)
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.ELECTRONIC.title), 400),
                Arguments.of(new Product()
                        .withTitle(faker.lorem().word())
                        .withPrice(faker.number().numberBetween(Integer.MIN_VALUE, -1))
                        .withCategoryTitle(Category.ELECTRONIC.title), 400),
                Arguments.of(new Product()
                        .withTitle(faker.lorem().word())
                        .withPrice(0)
                        .withCategoryTitle(Category.ELECTRONIC.title), 400)
        );
    }

    /**
     * Баг: Неверный код при создании продукта с параметрами:
     * title: пустое значение/null/специальные символы
     * price: <=0
     * ОР: 400; ФР: 201
     */
    @Issue("wrong.status.code")
    @DisplayName("Product POST - negative")
    @SneakyThrows
    @ParameterizedTest(name = "Product POST - negative: {0}; expectedCode={1}")
    @MethodSource("getInvalidProducts")
    public void productPostNegativeTest(Product product, int expectedCode) {
        Response<Product> response = productService.createProduct(product).execute();
        if (response.body() != null) {
            idToDelete = response.body().getId();
        } else idToDelete = null;
        assertThat(response.code()).isEqualTo(expectedCode);
    }

    @AfterEach
    public void deleteProduct() {
        if (idToDelete != null) {
            dbDeleteProduct(idToDelete);
        }
    }

}
