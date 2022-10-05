package homework6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.api.ProductService;
import org.example.db.dao.ProductsMapper;
import org.example.db.model.ProductsExample;
import org.example.dto.Product;
import org.example.utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ModifyProductTest {

    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    int id, price, newPrice;
    String title;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);

    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
    }

    void createProductInFoodCategoryTest() throws IOException {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id = response.body().getId();
        title = response.body().getTitle();
        price = response.body().getPrice();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        System.out.println(id + ", " + title + ", " + price);
    }

    @Test
    void putProductInFoodCategoryTest() throws IOException {
        createProductInFoodCategoryTest();
        ObjectMapper mapper = new ObjectMapper();
        StringReader reader = new StringReader("{ \"id\": 1, \"title\": \"" + title + "\", \"price\": 1000, \"categoryTitle\": \"Food\"}");
        Product product1 = mapper.readValue(reader, Product.class);

        Response<Product> response = productService.modifyProduct(product1)
                .execute();
        newPrice = response.body().getPrice();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getPrice(), equalTo(1000));

        System.out.println(id + ", " + title + ", previous price " + price + " , new price " + newPrice);

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession();
        ProductsMapper productsMapper = session.getMapper(ProductsMapper.class);
        ProductsExample productsExample = new ProductsExample();
        System.out.println(productsExample.createCriteria().andPriceEqualTo(newPrice).isValid());
        System.out.println(productsMapper.countByExample(productsExample));
        session.close();

    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        Response<ResponseBody> response = productService.deleteProduct(id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

}