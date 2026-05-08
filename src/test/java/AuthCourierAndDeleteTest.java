/*

Тест выполняет полный цикл работы с курьером:

1. Создаёт курьера с заранее заданным логином (не рандом) и проверяет, что создание прошло успешно.
2. Авторизует курьера и получает id, который сохраняется для удаления.
3. Автоматически удаляет курьера после теста - поддерживает чистоту тестовой среды.

 */


import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class AuthCourierAndDeleteTest extends CourierSteps {

    private static final String LOGIN = "dvgfdsgwfswa";
    private static final String PASSWORD = "password123";
    private static final String FIRST_NAME = "TestName";

    @Test
    @Feature("Курьеры")
    @Description("Создание курьера с корректными учётными данными, авторизация и удаление")
    public void testCourierAuth() {
        createAndVerifyCourier(LOGIN);
        getCourierIdForCleanup(LOGIN);
        performCourierAuth(LOGIN, PASSWORD);
    }

    @Step("Создание курьера с логином: {login}")
    private void createAndVerifyCourier(String login) {
        String password = PASSWORD;
        String firstName = FIRST_NAME;
        Response createResponse = createCourier(login, password, firstName);
        checkResponse(createResponse, 201, true);
    }

    @Step("Получение ID курьера для удаления: {login}")
    private void getCourierIdForCleanup(String login) {
        courierIdToDelete = authorizeCourier(login, PASSWORD);
    }

    @Step("Авторизация курьера: логин = {login}")
    private void performCourierAuth(String login, String password) {
        authCourier(login, password);
    }

    @Step("Проверка ответа: статус-код = {statusCode}, ok = {okValue}")
    private void checkResponse(Response response, int statusCode, boolean okValue) {
        response.then()
                .statusCode(statusCode)
                .body("ok", equalTo(okValue));
    }

    @Step("Авторизация курьера - вызов для видимости в отчёте Allure: логин = {login}")
    private void authCourier(String login, String password) {
        given()
                .spec(requestSpec)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParams("login", login, "password", password)
                .log().all()
                .when()
                .post(AUTH_COURIER_ENDPOINT)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().response();
    }
}
