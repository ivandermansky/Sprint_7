/*
Тест проверяет, что если авторизоваться под несуществующим курьером, запрос возвращает ошибку.
Для этого используется подбор рандомного логина с помощью currentTimeMillis()
*/

package test;

import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.BaseTestApi;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;

@Feature("Авторизация курьеров")
public class AuthWithNonExistingUserTest extends BaseTestApi {

    // Данные существующего пользователя (для сравнения)
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    // Создать экземпляр CourierSteps
    private CourierSteps courierSteps = new CourierSteps();

    @Test
    @Description("Проверка ошибки при авторизации с несуществующим логином")
    public void testAuthWithNonExistingLogin() {
        // Создать рандомный логин на основе currentTimeMillis()
        String randomLogin = "nonexistent_" + System.currentTimeMillis();
        String password = "any_password"; // пароль может быть любым

        System.out.println("Тестируемый логин (несуществующий): " + randomLogin);

        // Отправить запрос на авторизацию через метод из CourierSteps
        Response response = courierSteps.sendAuthRequestWithNonExistingUser(randomLogin, password);

        // Проверить ответ в тестовом классе
        checkAuthNonExistingUserResponse(response);
    }

    @Step("Проверка ответа авторизации с несуществующим пользователем: статус-код 404, сообщение об ошибке")
    private void checkAuthNonExistingUserResponse(Response response) {
        response.then()
                .log().ifError()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }
}
