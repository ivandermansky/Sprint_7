/*
Тест проверяет возвращение ошибки, если неправильно указать логин и/или пароль.
В тесте 4 проверки:

1. Проверяется успешная авторизация при правильно указанных логине и пароле.
2. Проверка ошибки при неверном логине
3. Проверка ошибки при неверном пароле
4. Проверка ошибки при неверных логине и пароле одновременно
*/

package test;

import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import models.BaseTestApi;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Авторизация курьеров")
public class CourierAuthWithInvalidCredentialsTest extends BaseTestApi {

    // Валидные и актуальные данные существующего пользователя
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    // Создать экземпляр CourierSteps для использования его методов
    private CourierSteps courierSteps = new CourierSteps();

    @Test
    @Description("Проверка успешной авторизации существующего курьера")
    public void testSuccessfulAuth() {
        // Отправить запрос успешной авторизации через CourierSteps
        Response response = courierSteps.authorizeCourier(EXISTING_LOGIN, EXISTING_PASSWORD);

        // Проверить успешный ответ в тестовом классе
        checkSuccessfulAuthResponse(response);
    }

    @Test
    @Description("Проверка ошибки при неверном логине")
    public void testAuthWithInvalidLogin() {
        String invalidLogin = "nonexistent_login";
        String validPassword = EXISTING_PASSWORD; // верный пароль

        // Отправить запрос авторизации с неверным логином через CourierSteps
        Response response = courierSteps.sendAuthRequest(invalidLogin, validPassword);

        // Проверить ответ с ошибкой в тестовом классе
        checkAuthErrorResponse(response, 404, "Учетная запись не найдена");
    }

    @Test
    @Description("Проверка ошибки при неверном пароле")
    public void testAuthWithInvalidPassword() {
        String validLogin = EXISTING_LOGIN; // верный логин
        String invalidPassword = "wrong_password";

        // Отправить запрос авторизации с неверным паролем через CourierSteps
        Response response = courierSteps.sendAuthRequest(validLogin, invalidPassword);

        // Проверить ответ с ошибкой в тестовом классе
        checkAuthErrorResponse(response, 404, "Учетная запись не найдена");
    }

    @Test
    @Description("Проверка ошибки при неверных логине и пароле")
    public void testAuthWithBothInvalid() {
        String invalidLogin = "nonexistent_login";
        String invalidPassword = "wrong_password";

        // Отправить запрос авторизации с неверными учётными данными через CourierSteps
        Response response = courierSteps.sendAuthRequest(invalidLogin, invalidPassword);

        // Проверить ответ с ошибкой в тестовом классе
        checkAuthErrorResponse(response, 404, "Учетная запись не найдена");
    }

    //Проверка успешного ответа авторизации: статус 200 и наличие поля id
    private void checkSuccessfulAuthResponse(Response response) {
        response.then()
                .log().ifError()
                .statusCode(200)
                .body("id", notNullValue());
    }

    /*
     - Проверка ответа при ошибке авторизации
     - response — полученный ответ
     - statusCode — ожидаемый HTTP статус-код
     - errorMessage — подстрока, которая должна содержаться в поле message тела ответа
     */
    private void checkAuthErrorResponse(Response response, int statusCode, String errorMessage) {
        response.then()
                .log().ifError()
                .statusCode(statusCode)
                .body("message", containsString(errorMessage));
    }
}
