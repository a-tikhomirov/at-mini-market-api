package ru.at.mini.market.api.category;

import io.qameta.allure.Feature;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;
import ru.at.mini.market.api.base.Base;
import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.db.model.ProductsExample;
import ru.at.mini.market.api.dto.GetCategoryResponse;
import ru.at.mini.market.api.dto.Product;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.at.mini.market.api.dto.DbRetrofitMapper.getProductsListFromDbProducts;

@Feature(value = "Category GET")
@DisplayName("Category GET")
public class GetCategoryTest extends Base {

    private static Stream<Arguments> getValidCategories() {
        ProductsExample foodProducts = new ProductsExample();
        foodProducts.createCriteria().andCategory_idEqualTo((long) Category.FOOD.id);
        ProductsExample electronicsProducts = new ProductsExample();
        electronicsProducts.createCriteria().andCategory_idEqualTo((long) Category.ELECTRONIC.id);
        return Stream.of(
                Arguments.of(Category.FOOD, getProductsListFromDbProducts(productsMapper.selectByExample(foodProducts))),
                Arguments.of(Category.ELECTRONIC, getProductsListFromDbProducts(productsMapper.selectByExample(electronicsProducts)))
        );
    }

    @DisplayName("Category GET - positive")
    @SneakyThrows
    @ParameterizedTest(name = "Category GET: {0}; List of products: {1}")
    @MethodSource("getValidCategories")
    public void categoryGetByIdPositiveTest(Category category, List<Product> productList) {
        Response<GetCategoryResponse> response = categoryService.getCategory(category.id).execute();
        assertThat(response)
                .satisfies(r -> assertThat(r.code()).isEqualTo(200))
                .satisfies(r -> assertThat(r.body()).isNotNull()
                        .satisfies(body -> assertThat(body.getId()).isEqualTo(category.id))
                        .satisfies(body -> assertThat(body.getTitle()).isEqualTo(category.title))
                        .satisfies(body -> body.getProducts().forEach(product ->
                                assertThat(product)
                                .satisfies(p -> assertThat(p.getId()).isInstanceOf(Integer.class).isGreaterThan(0))
                                .satisfies(p -> assertThat(p.getTitle()).isNotNull())
                                .satisfies(p -> assertThat(p.getPrice()).isInstanceOf(Integer.class))
                                .satisfies(p -> assertThat(p.getCategoryTitle()).isEqualTo(category.title)))
                        )
                );
        if (response.body() != null) {
            productList.forEach(product -> assertThat(response.body().getProducts()).contains(product));
        }
    }


    @DisplayName("Category GET - negative: wrong id")
    @SneakyThrows
    @Test
    public void categoryGetWrongIdNegativeTest() {
        Response<GetCategoryResponse> response = categoryService.getCategory(faker.number().numberBetween(-100, -1)).execute();
        assertThat(response.code()).isEqualTo(404);
    }

    @DisplayName("Category GET - negative: illegal type id")
    @SneakyThrows
    @Test
    public void categoryGetIllegalTypeIdNegativeTest() {
        Response<GetCategoryResponse> response = categoryService.getCategory(faker.lorem().fixedString(1)).execute();
        assertThat(response.code()).isEqualTo(400);
    }

}
