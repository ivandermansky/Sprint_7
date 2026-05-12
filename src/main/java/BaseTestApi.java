package models;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;

public class BaseTestApi {
    protected static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    protected static RequestSpecification requestSpec;
    protected static final String LOGIN_ENDPOINT = "/api/v1/courier/login";


    @BeforeClass
    public static void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json; charset=UTF-8")
                .build();
    }

}
