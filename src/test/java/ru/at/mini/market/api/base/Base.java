package ru.at.mini.market.api.base;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import retrofit2.Retrofit;
import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.db.dao.CategoriesMapper;
import ru.at.mini.market.api.db.dao.ProductsMapper;
import ru.at.mini.market.api.db.model.Products;
import ru.at.mini.market.api.dto.Product;
import ru.at.mini.market.api.services.CategoryService;
import ru.at.mini.market.api.services.ProductService;
import ru.at.mini.market.api.utils.RetrofitUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static ru.at.mini.market.api.dto.DbRetrofitMapper.getRetrofitProductFromDbProduct;
import static ru.at.mini.market.api.utils.PropertyLoader.loadProperty;

public abstract class Base {

    private static final String MYBATIS_CONFIG = "mybatis-config.xml";
    private static final Retrofit RETROFIT = RetrofitUtils.getRetrofit();

    protected static Faker faker = new Faker();
    protected static CategoryService categoryService;
    protected static ProductService productService;
    protected static CategoriesMapper categoriesMapper;
    protected static ProductsMapper productsMapper;

    @BeforeAll
    public static void createServices() {
        categoryService = RETROFIT.create(CategoryService.class);
        productService = RETROFIT.create(ProductService.class);
    }

    @BeforeAll
    public static void createMappers() {
        try {
            InputStream is = Resources.getResourceAsStream(MYBATIS_CONFIG);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
            SqlSession session = sqlSessionFactory.openSession(true);

            categoriesMapper = session.getMapper(CategoriesMapper.class);
            productsMapper = session.getMapper(ProductsMapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Step("Create new product")
    protected int dbCreateProduct() {
        return dbCreateProduct(
                new Product()
                        .withTitle(faker.food().ingredient())
                        .withPrice(faker.number().numberBetween(1, 100))
                        .withCategoryTitle(Category.FOOD.title));
    }

    @Step("Create new product")
    protected int dbCreateProduct(Product product) {
        Products newProduct = new Products();
        newProduct.setTitle(product.getTitle());
        newProduct.setPrice(product.getPrice());
        newProduct.setCategory_id((long) Category.valueOf(product.getCategoryTitle().toUpperCase()).id);
        productsMapper.insertSelective(newProduct);
        return Math.toIntExact(newProduct.getId());
    }

    @Step("Get product by id")
    protected Product dbGetProductById(int id) {
        return getRetrofitProductFromDbProduct(productsMapper.selectByPrimaryKey((long) id));
    }

    @Step("Delete product with id={id}")
    protected void dbDeleteProduct(int id) {
        productsMapper.deleteByPrimaryKey((long) id);
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
                        .withCategoryTitle(Category.ELECTRONIC.title),
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
                        .withCategoryTitle(Category.ELECTRONIC.title),
                new Product()
                        .withTitle(loadProperty("text.ru.word"))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.FOOD.title),
                new Product()
                        .withTitle(loadProperty("text.ru.phrase"))
                        .withPrice(faker.number().numberBetween(1 , Integer.MAX_VALUE - 1))
                        .withCategoryTitle(Category.ELECTRONIC.title)
        );
    }

}
