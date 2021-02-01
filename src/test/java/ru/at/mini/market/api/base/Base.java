package ru.at.mini.market.api.base;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.dto.Product;
import ru.at.mini.market.api.services.CategoryService;
import ru.at.mini.market.api.services.ProductService;
import ru.at.mini.market.api.utils.RetrofitUtils;

import java.util.stream.Stream;

import static ru.at.mini.market.api.utils.PropertyLoader.loadProperty;

public abstract class Base {

    private static final Retrofit retrofit = RetrofitUtils.getRetrofit();

    protected static Faker faker = new Faker();
    protected static CategoryService categoryService;
    protected static ProductService productService;

    @BeforeAll
    public static void createServices() {
        categoryService = retrofit.create(CategoryService.class);
        productService = retrofit.create(ProductService.class);
    }

    @SneakyThrows
    @Step("Creat new product")
    protected Response<Product> createProduct() {
        Product product = new Product()
                .withTitle(faker.food().ingredient())
                .withPrice(faker.number().numberBetween(1, 100))
                .withCategoryTitle(Category.FOOD.title);
        return productService.createProduct(product).execute();
    }

    @SneakyThrows
    @Step("Delete product with id={id}")
    protected Response<ResponseBody> deleteProduct(int id) {
        return productService.deleteProduct(id).execute();
    }

    protected static Stream<Product> getValidProducts() {
        return Stream.of(
                new Product()
                        .withTitle(faker.lorem().sentence(2))
                        .withPrice(Integer.MAX_VALUE)
                        .withCategoryTitle(Category.FOOD.title),
                new Product()
                        .withTitle(faker.lorem().word())
                        .withPrice(Integer.MAX_VALUE)
                        .withCategoryTitle(Category.ELECTRONICS.title),
                new Product()
                        .withTitle(faker.lorem().sentence(2))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title),
                new Product()
                        .withTitle(loadProperty("text.ru.word"))
                        .withPrice(Integer.MAX_VALUE)
                        .withCategoryTitle(Category.FOOD.title),
                new Product()
                        .withTitle(loadProperty("text.ru.phrase"))
                        .withPrice(Integer.MAX_VALUE)
                        .withCategoryTitle(Category.ELECTRONICS.title),
                new Product()
                        .withTitle(loadProperty("text.ru.word"))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title),
                new Product()
                        .withTitle(loadProperty("text.ru.phrase"))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.ELECTRONICS.title)
        );
    }

}
