package Steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.BeforeClass;
import models.CourierTestData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierSteps {

    protected static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    protected static final String CREATE_COURIER_ENDPOINT = "/api/v1/courier";
    protected static final String AUTH_COURIER_ENDPOINT = "/api/v1/courier/login";
    protected static final String DELETE_COURIER_ENDPOINT = "/api/v1/courier/{courierId}";
    protected static RequestSpecification requestSpec;

    // Поле для хранения ID курьера
    protected int courierIdToDelete = -1;

    @BeforeClass
    public static void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json; charset=UTF-8")
                .build();
    }

    /*
     - Метод создания курьера
     */
    @Step("Создание курьера со случайным логином: {login}")
    protected Response createCourier(String login, String password, String firstName) {
        CourierTestData courierData = new CourierTestData(
                login,
                password,
                firstName
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

    /*
     * Метод авторизации курьера для получения ID
     */
    @Step("Авторизация курьера для получения ID: {login}")
    protected int authorizeCourier(String login, String password) {
        CourierTestData authData = new CourierTestData();
        authData.setLogin(login);
        authData.setPassword(password); // Теперь password передаётся как параметр

        Response authResponse = given()
                .spec(requestSpec)
                .body(authData)
                .log().all()
                .when()
                .post(AUTH_COURIER_ENDPOINT)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().response();

        return authResponse.jsonPath().getInt("id");
    }

    /*
     * Удаление курьера после теста
     */
    @After
    @Step("Удаление курьера по ID: {courierId}")
    public void deleteCourier() {
        if (courierIdToDelete > -1) {
            given()
                    .spec(requestSpec)
                    .pathParam("courierId", courierIdToDelete)
                    .log().all()
                    .when()
                    .delete(DELETE_COURIER_ENDPOINT)
                    .then()
                    .log().ifError()
                    .log().all()
                    .statusCode(200)
                    .body("ok", equalTo(true));

            courierIdToDelete = -1;
        }
    }
}
