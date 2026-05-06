import io.restassured.response.Response;
import org.junit.Test;

import io.qameta.allure.Step;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateCourierWithRandomNameAuthAndDeleteTest extends BaseTestApi {
    private static final String CREATECOURIERENDPOINT = "/api/v1/courier";
    private static final String AUTHCOURIERENDPOINT = "/api/v1/courier/login";
    private static final String DELETECOURIERENDPOINT = "/api/v1/courier/{courierId}";

    @Test
    @Feature("Курьеры")
    @Description("Создание, авторизация и удаление курьера со случайным логином")
    public void testCreateAuthAndDeleteCourier() {
        String uniqueLogin = "testcourier_" + System.currentTimeMillis();

        Response createResponse = createCourier(uniqueLogin);
        int courierId = authorizeCourier(uniqueLogin);
        deleteCourier(courierId);
    }

    @Step("Создание курьера со случайным логином: {login}")
    protected Response createCourier(String login) {
        // Создать объект CourierTestData
        CourierTestData courierData = new CourierTestData(
                login,
                "password123",
                "Василий"
        );

        return given()
                .spec(requestSpec)
                .body(courierData)  // передать объект CourierTestData
                .log().all()
                .when()
                .post(CREATECOURIERENDPOINT)
                .then()
                .log().ifError()
                .log().all()
                .statusCode(201)
                .body("ok", equalTo(true))
                .extract().response();
    }

    @Step("Авторизация курьера для получения ID: {login}")
    protected int authorizeCourier(String login) {

        CourierTestData authData = new CourierTestData();
        authData.setLogin(login);
        authData.setPassword("password123");
        // firstName не нужен для авторизации, можно оставить пустым

        Response authResponse = given()
                .spec(requestSpec)
                .body(authData)  // передать объект CourierTestData
                .log().all()
                .when()
                .post(AUTHCOURIERENDPOINT)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().response();

        return authResponse.jsonPath().getInt("id");
    }

    @Step("Удаление курьера по ID: {courierId}")
    protected void deleteCourier(int courierId) {
        given()
                .spec(requestSpec)
                .pathParam("courierId", courierId)
                .log().all()
                .when()
                .delete(DELETECOURIERENDPOINT)
                .then()
                .log().ifError()
                .log().all()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
}
