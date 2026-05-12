/*
Тест проверяет, что система возвращает ошибку если не передать какое-то из необходимых полей (логин или пароль).
Система должна возвращать 400 и {"message": "Недостаточно данных для входа"}.
При тесте, в котором проверяется авторизация без пароля возвращается ошибка 504, что свидетельствует о баге: сервер некорректно обрабатывает null
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
public class AuthWithMissingFieldsTest extends BaseTestApi {

    // Будут использованы те же данные, что и в AuthOnlyTest
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    // Создать экземпляр CourierSteps
    private CourierSteps courierSteps = new CourierSteps();

    @Test
    @Description("Проверка ошибки при отсутствии поля login в запросе авторизации")
    public void testAuthWithoutLogin() {
        // Отправить запрос через метод из CourierSteps (метод принимает только пароль)
        Response response = courierSteps.sendAuthRequestWithoutLogin(EXISTING_PASSWORD);

        // Проверить ответ в тестовом классе
        checkAuthMissingLoginResponse(response);
    }

    @Test
    @Description("Проверка ошибки при отсутствии поля password в запросе авторизации")
    public void testAuthWithoutPassword() {
        // Отправить запрос через метод из CourierSteps (метод принимает только логин)
        Response response = courierSteps.sendAuthRequestWithoutPassword(EXISTING_LOGIN);

        // Проверить ответ в тестовом классе
        checkAuthMissingPasswordResponse(response);
    }

    @Step("Проверка ответа авторизации при отсутствии поля login: статус-код 400, сообщение об ошибке")
    private void checkAuthMissingLoginResponse(Response response) {
        response.then()
                .log().ifError()
                .log().all()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Step("Проверка ответа авторизации при отсутствии поля password: статус-код 400, сообщение об ошибке")
    private void checkAuthMissingPasswordResponse(Response response) {
        response.then()
                .log().ifError()
                .log().all()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }
}
