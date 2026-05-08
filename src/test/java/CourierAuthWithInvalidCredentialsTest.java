/*
Тест проверяет возвращение ошибки, если неправильно указать логин и/или пароль.
В тесте 4 проверки:

1. Проверяется успешная авторизация при правильно указанных логине и пароле.
2. Проверка ошибки при неверном логине
3. Проверка ошибки при неверном пароле
4. Проверка ошибки при неверных логине и пароле одновременно
*/

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CourierTestData;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Авторизация курьеров")
public class CourierAuthWithInvalidCredentialsTest extends BaseTestApi {

    // Валидные и актуальные данные существующего пользователя
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    @Test
    @Description("Проверка успешной авторизации существующего курьера")
    public void testSuccessfulAuth() {
        // Формируем данные для авторизации
        CourierTestData authData = new CourierTestData();
        authData.setLogin(EXISTING_LOGIN);
        authData.setPassword(EXISTING_PASSWORD);

        Response response = sendAuthRequest(authData, "успешная авторизация");

        // Проверяем успешный ответ: статус 200 и наличие поля id
        response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .log().ifError();
    }

    @Test
    @Description("Проверка ошибки при неверном логине")
    public void testAuthWithInvalidLogin() {
        CourierTestData authData = new CourierTestData();
        authData.setLogin("nonexistent_login");
        authData.setPassword(EXISTING_PASSWORD); // верный пароль

        Response response = sendAuthRequest(authData, "неверный логин");
        checkAuthErrorResponse(response, 404, "Учетная запись не найдена");
    }

    @Test
    @Description("Проверка ошибки при неверном пароле")
    public void testAuthWithInvalidPassword() {
        CourierTestData authData = new CourierTestData();
        authData.setLogin(EXISTING_LOGIN); // верный логин
        authData.setPassword("wrong_password");

        Response response = sendAuthRequest(authData, "неверный пароль");
        checkAuthErrorResponse(response, 404, "Учетная запись не найдена");
    }

    @Test
    @Description("Проверка ошибки при неверных логине и пароле")
    public void testAuthWithBothInvalid() {
        CourierTestData authData = new CourierTestData();
        authData.setLogin("nonexistent_login");
        authData.setPassword("wrong_password");

        Response response = sendAuthRequest(authData, "неверные логин и пароль");
        checkAuthErrorResponse(response, 404, "Учетная запись не найдена");
    }

    /**
     * Метод отправки запроса авторизации курьера
     */
    @Step("Отправка запроса авторизации: сценарий = '{scenario}', логин = '{authData.login}', пароль = '{authData.password}'")
    private Response sendAuthRequest(CourierTestData authData, String scenario) {
        return given()
                .spec(requestSpec)
                .body(authData)
                .log().all() // логируем отправляемые данные
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .extract().response();
    }

    /*
     * Проверка ответа при ошибке авторизации
     */
    @Step("Проверка ответа при ошибке авторизации: статус = {statusCode}, сообщение содержит '{errorMessage}'")
    private void checkAuthErrorResponse(Response response, int statusCode, String errorMessage) {
        response.then()
                .statusCode(statusCode)
                .body("message", containsString(errorMessage));
    }
}
