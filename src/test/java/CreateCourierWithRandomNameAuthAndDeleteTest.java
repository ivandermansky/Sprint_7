
/*

1. Тест создает курьера со случайным логином с помощью currentTimeMillis()
2. Тест авторизует курьера
3. Тест удаляет курьера

 */

package tests;

import Steps.CourierSteps;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.qameta.allure.Step;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Курьеры")
public class CreateCourierWithRandomNameAuthAndDeleteTest {

    // Создать экземпляр CourierSteps
    private static CourierSteps courierSteps = new CourierSteps();
    // Поле для хранения ID курьера (локальное для теста)
    private static int courierIdToDelete = -1;

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
    @Description("Создание, авторизация и удаление курьера со случайным логином")
    public void testCreateAuthAndDeleteCourier() {
        String uniqueLogin = "testcourier_" + System.currentTimeMillis();
        String password = "password123";
        String firstName = "Василий";

        // Отправить запрос создания курьера через CourierSteps
        Response createResponse = courierSteps.createCourier(uniqueLogin, password, firstName);
        // Проверить ответ создания курьера в тестовом классе
        checkCreateCourierResponse(createResponse);

        // Авторизовать курьера и получаем ID через CourierSteps
        Response authResponse = courierSteps.authorizeCourier(uniqueLogin, password);
        // Проверить ответ авторизации в тестовом классе
        checkAuthResponse(authResponse);
        // Сохранить ID для автоматического удаления после теста
        courierIdToDelete = authResponse.jsonPath().getInt("id");
    }

    @Step("Проверка ответа создания курьера: статус-код 201, ok = true")
    private void checkCreateCourierResponse(Response response) {
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

    @Step("Проверка ответа удаления курьера: статус-код 200, ok = true")
    private static void checkDeleteCourierResponse(Response response) {
        response.then()
                .log().all()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
}
