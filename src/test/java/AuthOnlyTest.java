/*
Тест проверяет успешную авторизацию заранее созданного курьера.
Логин и пароль данного курьера будут далее использоваться:
- для теста на возвращение ошибки при неправильном логине и/или пароле
- для теста, который проверяет возвращение id при авторизации
*/


package test;

import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

@Feature("Авторизация курьеров")
public class AuthOnlyTest {

    // Данные существующего пользователя
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    // Создать экземпляр CourierSteps
    private static CourierSteps courierSteps;

    @BeforeClass
    public static void setup() {
        // Инициализировать requestSpec через метод setup в CourierSteps
        CourierSteps.setup();
        courierSteps = new CourierSteps();
    }

    @Test
    @Description("Проверка успешной авторизации существующего курьера")
    public void testSuccessfulAuth() {
        // Отправить запрос на авторизацию через метод из CourierSteps
        Response response = courierSteps.authorizeCourier(EXISTING_LOGIN, EXISTING_PASSWORD);

        // Проверить ответ в тестовом классе
        checkSuccessfulAuthResponse(response);
    }

    @Step("Проверка успешного ответа авторизации: статус 200 и наличие поля 'id'")
    private void checkSuccessfulAuthResponse(Response response) {
        response.then()
                .log().ifError()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue());
    }
}
