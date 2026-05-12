
/*
Тест проверяет, что невозможно создать курьера или без логина или без пароля.
Оба теста проходят успешно - система возвращает ошибку в обоих случаях.
 */

package tests;

import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;

@Feature("Курьеры")
public class CreateCourierWithMissingFieldsTest {

    private static final String VALID_LOGIN = "test_login_" + System.currentTimeMillis();
    private static final String VALID_PASSWORD = "password123";
    private static final String VALID_FIRST_NAME = "Василий";

    // Создать экземпляр CourierSteps
    private static CourierSteps courierSteps = new CourierSteps();

    @BeforeClass
    public static void setup() {
        // Инициализировать requestSpec через метод setup в CourierSteps
        courierSteps.setup();
    }

    @Test
    @Description("Проверка ошибки при отсутствии поля login в запросе создания курьера")
    public void testCreateCourierWithoutLogin() {
        // Отправить запрос создания курьера без логина через CourierSteps
        Response response = courierSteps.createCourierWithoutLogin(VALID_PASSWORD, VALID_FIRST_NAME);

        // Проверить ответ в тестовом классе
        checkMissingLoginResponse(response);
    }

    @Test
    @Description("Проверка ошибки при отсутствии поля password в запросе создания курьера")
    public void testCreateCourierWithoutPassword() {
        // Отправить запрос создания курьера без пароля через CourierSteps
        Response response = courierSteps.createCourierWithoutPassword(VALID_LOGIN, VALID_FIRST_NAME);

        // Проверить ответ в тестовом классе
        checkMissingPasswordResponse(response);
    }

    @Step("Проверка ответа при отсутствии поля login: статус-код 400, сообщение содержит 'Недостаточно данных для создания учетной записи'")
    private void checkMissingLoginResponse(Response response) {
        response.then()
                .log().all()  // Логирование полного ответа сервера
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Step("Проверка ответа при отсутствии поля password: статус-код 400, сообщение содержит 'Недостаточно данных для создания учетной записи'")
    private void checkMissingPasswordResponse(Response response) {
        response.then()
                .log().all()  // Логирование полного ответа сервера
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }
}
