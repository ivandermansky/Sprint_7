
/*
Тест проверяет, что система не даёт создать двух курьеров с одинаковым логином.
Курьер создаётся два раза — первый раз успешно, второй раз — ошибка.
В конце теста созданный курьер удаляется.
*/

package test;

import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNull.notNullValue;
import java.util.Random;

@Feature("Курьеры")
public class CreateCourierWithDuplicateLoginTest {

    private static final String PASSWORD = "password123";
    private static final String FIRST_NAME = "Василий";

    // Создать экземпляр CourierSteps
    private static CourierSteps courierSteps = new CourierSteps();
    // Поле для хранения ID курьера (локальное для теста)
    private static int courierIdToDelete = -1;

    // Генерировать уникальный логин для каждого запуска теста
    private String generateUniqueLogin() {
        return "test_login_" + new Random().nextInt(10000);
    }

    @BeforeClass
    public static void setup() {
        // Инициализировать requestSpec через метод setup в CourierSteps
        courierSteps.setup();
    }

    @AfterClass
    public static void cleanup() {
        // Удалить курьера после выполнения всех тестов, если ID был получен
        if (courierIdToDelete > 0) {
            Response deleteResponse = courierSteps.deleteCourierById(courierIdToDelete);
            checkDeleteCourierResponse(deleteResponse);
        }
    }

    @Test
    @Description("Проверка невозможности создания курьера с уже используемым логином")
    public void testCreateCourierWithDuplicateLogin() {
        // Генерировать уникальный логин
        String uniqueLogin = generateUniqueLogin();

        // Отправить запрос создания курьера с уникальным логином через CourierSteps
        Response firstCreateResponse = courierSteps.createCourier(uniqueLogin, PASSWORD, FIRST_NAME);
        // Проверить успешный ответ создания курьера в тестовом классе
        checkSuccessfulCreateResponse(firstCreateResponse);

        // Получить ID курьера через авторизацию
        Response authResponse = courierSteps.authorizeCourier(uniqueLogin, PASSWORD);
        // Проверить ответ авторизации в тестовом классе
        checkAuthResponse(authResponse);
        // Сохранить ID для автоматического удаления после теста
        courierIdToDelete = authResponse.jsonPath().getInt("id");

        // Попытаться создать курьера с тем же логином (ошибка)
        Response secondCreateResponse = courierSteps.createCourier(uniqueLogin, PASSWORD, FIRST_NAME);
        // Проверить ответ при дублирующемся логине в тестовом классе
        checkDuplicateLoginResponse(secondCreateResponse, 409, "Этот логин уже используется");
    }

    @Step("Проверка успешного ответа создания курьера: статус-код 201, ok = true")
    private void checkSuccessfulCreateResponse(Response response) {
        response.then()
                .log().ifError()
                .log().all()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Проверка ответа авторизации: статус-код 200, поле id присутствует")
    private void checkAuthResponse(Response response) {
        response.then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Проверка ответа при попытке создания курьера с дублирующимся логином: статус = {statusCode}, сообщение содержит '{errorMessage}'")
    private void checkDuplicateLoginResponse(Response response, int statusCode, String errorMessage) {
        response.then()
                .log().all()
                .statusCode(statusCode)
                .body("message", containsString(errorMessage));
    }

    @Step("Проверка ответа удаления курьера: статус-код 200, ok = true")
    private static void checkDeleteCourierResponse(Response response) {
        response.then()
                .log().all()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
}
