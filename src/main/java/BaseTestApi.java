import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;

public class BaseTestApi {
    protected static final String BASEURL = "https://qa-scooter.praktikum-services.ru";
    protected static RequestSpecification requestSpec;

    @BeforeClass
    public static void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASEURL)
                .setContentType("application/json; charset=UTF-8")
                .build();
    }
}