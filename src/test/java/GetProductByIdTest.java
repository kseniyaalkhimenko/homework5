
import org.example.api.ProductService;
import org.example.dto.Product;
import org.example.utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetProductByIdTest {

    static ProductService productService;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @Test
    void getProductTest() throws IOException {
        Response<Product> response = productService.getProductById(5)
                .execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getCategoryTitle(), equalTo("Electronic"));
        assertThat(response.body().getTitle(), equalTo("LG TV 1"));
    }

}