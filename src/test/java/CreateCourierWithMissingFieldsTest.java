

/*
Тест проверяет, что невозможно создать курьера или без логина или без пароля.
Оба теста проходят успешно - система возвращает ошибку в обоих случаях.
 */

package tests;

import Steps.CourierSteps;
import models.CourierTestData;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@Feature("Курьеры")
public class CreateCourierWithMissingFieldsTest extends CourierSteps {

    private static final String VALID_LOGIN = "test_login_" + System.currentTimeMillis();
    private static final String VALID_PASSWORD = "password123";
    private static final String VALID_FIRST_NAME = "Василий";

    @Test
    @Description("Проверка ошибки при отсутствии поля 'login' в запросе создания курьера")
    public void testCreateCourierWithoutLogin() {
        // Создать данные курьера без логина
        CourierTestData courierData = new CourierTestData();
        courierData.setPassword(VALID_PASSWORD);
        courierData.setFirstName(VALID_FIRST_NAME);

        // Отправить запрос и проверить ответ
        Response response = createCourierWithData(courierData);
        checkMissingFieldsResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Description("Проверка ошибки при отсутствии поля 'password' в запросе создания курьера")
    public void testCreateCourierWithoutPassword() {
        // Создать данные курьера без пароля
        CourierTestData courierData = new CourierTestData();
        courierData.setLogin(VALID_LOGIN);
        courierData.setFirstName(VALID_FIRST_NAME);

        // Отправить запрос и проверить ответ
        Response response = createCourierWithData(courierData);
        checkMissingFieldsResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }


     // Метод создания курьера с использованием объекта CourierTestData. Отправляет POST‑запрос на создание курьера с переданными данными и возвращает ответ сервера.
    @Step("Создание курьера с данными: логин = {courierData.login}, пароль = {courierData.password}")
    protected Response createCourierWithData(CourierTestData courierData) {
        return given()
                .spec(requestSpec)
                .body(courierData)
                .log().all()
                .when()
                .post(CREATE_COURIER_ENDPOINT)
                .then()
                .log().ifError()
                .extract().response();
    }

    @Step("Проверка ответа при отсутствии обязательных полей: статус = {statusCode}, сообщение содержит '{errorMessage}'")
    private void checkMissingFieldsResponse(Response response, int statusCode, String errorMessage) {
        response.then()
                .statusCode(statusCode)
                .body("message", containsString(errorMessage));
    }
}
