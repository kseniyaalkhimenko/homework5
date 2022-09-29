import okhttp3.ResponseBody;
import org.example.api.ProductService;
import org.example.utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetProductsTest {

    static ProductService productService;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @Test
    void getProductFoodTest() throws IOException {
        Response<ResponseBody> response = productService.getProducts()
                .execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().string(), containsString("Food"));
    }

    @Test
    void getProductElectronicTest() throws IOException {
        Response<ResponseBody> response = productService.getProducts()
                .execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().string(), containsString("Electronic"));
    }
}
