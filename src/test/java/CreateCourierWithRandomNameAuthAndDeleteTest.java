/*

1. Тест создает курьера со случайным логином с помощью currentTimeMillis()
2. Тест авторизует курьера
3. Тест удаляет курьера


 */

import Steps.CourierSteps;
import io.restassured.response.Response;
import models.CourierTestData;
import org.junit.Test;

import io.qameta.allure.Step;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateCourierWithRandomNameAuthAndDeleteTest extends CourierSteps {

    @Test
    @Feature("Курьеры")
    @Description("Создание, авторизация и удаление курьера со случайным логином")
    public void testCreateAuthAndDeleteCourier() {
        String uniqueLogin = "testcourier_" + System.currentTimeMillis();

        Response createResponse = createCourier(uniqueLogin);
        // Сохранить ID для автоматического удаления после теста
        courierIdToDelete = authorizeCourier(uniqueLogin);
    }

    @Step("Создание курьера со случайным логином: {login}")
    protected Response createCourier(String login) {
        CourierTestData courierData = new CourierTestData(
                login,
                "password123",
                "Василий"
        );

        return given()
                .spec(requestSpec)
                .body(courierData)
                .log().all()
                .when()
                .post(CREATE_COURIER_ENDPOINT)
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

        Response authResponse = given()
                .spec(requestSpec)
                .body(authData)
                .log().all()
                .when()
                .post(AUTH_COURIER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().response();

        return authResponse.jsonPath().getInt("id");
    }
}
