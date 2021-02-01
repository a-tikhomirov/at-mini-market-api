package ru.at.mini.market.api.category;

import io.qameta.allure.Feature;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;
import ru.at.mini.market.api.base.Base;
import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.dto.GetCategoryResponse;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Feature(value = "Category GET")
@DisplayName("Category GET")
public class GetCategoryTest extends Base {

    private static Stream<Category> getValidCategories() {
        return Arrays.stream(Category.values().clone());
    }

    @DisplayName("Category GET - positive")
    @SneakyThrows
    @ParameterizedTest(name = "Category GET: {0}")
    @MethodSource("getValidCategories")
    public void categoryGetByIdPositiveTest(Category category) {
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
