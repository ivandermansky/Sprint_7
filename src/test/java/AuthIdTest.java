// Тест проверяет, что при авторизации курьера возвращается id курьера

package test;

import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Авторизация курьеров")
public class AuthIdTest {

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
    @Description("Проверка успешной авторизации существующего курьера и возврата корректного ID")
    public void testSuccessfulAuthWithIdValidation() {
        // Отправить запрос на авторизацию через метод из CourierSteps
        Response response = courierSteps.authorizeCourier(EXISTING_LOGIN, EXISTING_PASSWORD);

        // Проверки в тестовом классе
        checkSuccessfulAuthResponse(response);
        validateUserIdInResponse(response);
    }

    @Step("Проверка успешного ответа авторизации: статус 200 и наличие поля id")
    private void checkSuccessfulAuthResponse(Response response) {
        response.then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Валидация ID пользователя в ответе")
    private void validateUserIdInResponse(Response response) {
        Integer userId = response.jsonPath().get("id");

        System.out.println("Полученный ID пользователя: " + userId);

        if (userId == null) {
            throw new AssertionError("ID пользователя не должен быть null");
        }

        if (userId <= 0) {
            throw new AssertionError("ID пользователя должен быть больше нуля, но получен: " + userId);
        }
    }
}
