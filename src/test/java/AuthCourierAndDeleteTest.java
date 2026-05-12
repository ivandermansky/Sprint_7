/*

Тест выполняет полный цикл работы с курьером:

1. Создаёт курьера с заранее заданным логином (не рандом) и проверяет, что создание прошло успешно.
2. Авторизует курьера и получает id, который сохраняется для удаления.
3. Автоматически удаляет курьера после теста - поддерживает чистоту тестовой среды.

 */


package test;

import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@Feature("Курьеры")
public class AuthCourierAndDeleteTest {

    private static final String LOGIN = "dvgfdsgwfswa1";
    private static final String PASSWORD = "password123";
    private static final String FIRST_NAME = "TestName";

    // Создать экземпляр CourierSteps
    private static CourierSteps courierSteps;

    @BeforeClass
    public static void setup() {
        // Инициализировать requestSpec через метод setup в CourierSteps
        CourierSteps.setup();
        courierSteps = new CourierSteps();
    }

    @Test
    @Description("Создание курьера с корректными учётными данными, авторизация и удаление")
    public void testCourierAuth() {
        int courierId = createAndVerifyCourier(LOGIN);
        performCourierAuth(LOGIN, PASSWORD);
        deleteAndVerifyCourier(courierId);
    }

    @Step("Создание курьера с логином: {login}")
    private int createAndVerifyCourier(String login) {
        String password = PASSWORD;
        String firstName = FIRST_NAME;

        // Отправить запрос создания курьера через CourierSteps
        Response createResponse = courierSteps.createCourier(login, password, firstName);

        // Проверить ответ в тестовом классе
        checkCreateCourierResponse(createResponse);

        // Получить ID курьера для последующего удаления
        Response authResponse = courierSteps.authorizeCourier(login, password);
        courierSteps.checkSuccessfulAuthResponse(authResponse);
        return authResponse.jsonPath().getInt("id");
    }

    @Step("Авторизация курьера: логин = {login}")
    private void performCourierAuth(String login, String password) {
        // Отправить запрос авторизации через CourierSteps
        Response authResponse = courierSteps.authorizeCourier(login, password);

        // Проверить успешный ответ в тестовом классе
        courierSteps.checkSuccessfulAuthResponse(authResponse);
    }

    @Step("Удаление курьера и проверка успешности")
    private void deleteAndVerifyCourier(int courierId) {
        if (courierId > -1) {
            // Отправить запрос удаления через CourierSteps
            Response deleteResponse = courierSteps.deleteCourierById(courierId);

            // Проверить ответ удаления в тестовом классе
            checkDeleteCourierResponse(deleteResponse);
        }
    }

    @Step("Проверка ответа создания курьера: статус-код 201, ok = true")
    private void checkCreateCourierResponse(Response response) {
        response.then()
                .log().ifError()
                .log().all()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Проверка ответа удаления курьера: статус-код 200, ok = true")
    private void checkDeleteCourierResponse(Response response) {
        response.then()
                .log().all()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
}
