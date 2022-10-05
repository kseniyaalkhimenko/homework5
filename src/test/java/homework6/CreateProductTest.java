package homework6;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.example.api.ProductService;
import org.example.db.dao.ProductsMapper;
import org.example.db.model.ProductsExample;
import org.example.dto.Product;
import org.example.utils.RetrofitUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateProductTest {

    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    long id;

    @BeforeAll
    static void beforeAll() throws IOException {
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

    @Test
    void createProductInFoodCategoryTest() throws IOException {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id = response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        System.out.println(id);

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession();
        ProductsMapper productsMapper = session.getMapper(ProductsMapper.class);
        ProductsExample productsExample = new ProductsExample();
        System.out.println(productsExample.createCriteria().andIdEqualTo(id).isValid());
        System.out.println(productsMapper.countByExample(productsExample));
        session.close();
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        Response<ResponseBody> response = productService.deleteProduct((int) id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

}