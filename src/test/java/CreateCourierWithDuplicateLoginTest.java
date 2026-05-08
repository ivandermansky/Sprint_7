/*
Тест проверяет, что система не даёт создать двух курьеров с одинаковым логином.
Курьер создаётся два раза — первый раз успешно, второй раз — ошибка.
В конце теста созданный курьер удаляется.
*/

import Steps.CourierSteps;
import models.CourierTestData;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;

public class CreateCourierWithDuplicateLoginTest extends CourierSteps {

    private static final String DUPLICATE_LOGIN = "test_existing_login";
    private static final String PASSWORD = "password123";
    private static final String FIRST_NAME = "Василий";

    @Test
    @Feature("Курьеры")
    @Description("Проверка невозможности создания курьера с уже используемым логином")
    public void testCreateCourierWithDuplicateLogin() {
        // Создать объект данных для первого курьера
        CourierTestData firstCourierData = new CourierTestData(DUPLICATE_LOGIN, PASSWORD, FIRST_NAME);

        // Создать курьера с фиксированным логином (успех)
        Response firstCreateResponse = createCourierWithData(firstCourierData);
        checkResponse(firstCreateResponse, 201, true);

        // Сохранять ID для удаления после теста
        courierIdToDelete = authorizeCourier(DUPLICATE_LOGIN, PASSWORD); // Передаём login и password

        // Попытаться создать курьера с тем же логином (ошибка)
        Response secondCreateResponse = createCourierWithData(firstCourierData);
        checkDuplicateLoginResponse(secondCreateResponse, 409, "Этот логин уже используется");
    }

    // Метод создания курьера с использованием объекта CourierTestData
    @Step("Создание курьера: логин = {courierData.login}")
    protected Response createCourierWithData(CourierTestData courierData) {
        return given()
                .spec(requestSpec)
                .body(courierData)
                .log().all()
                .when()
                .post(CREATE_COURIER_ENDPOINT)
                .then()
                .log().ifError()
                .log().all()
                .extract().response();
    }

    @Step("Проверка ответа: статус-код = {statusCode}, ok = {okValue}")
    private void checkResponse(Response response, int statusCode, boolean okValue) {
        response.then()
                .statusCode(statusCode)
                .body("ok", equalTo(okValue));
    }

    @Step("Проверка ответа при попытке создания курьера с дублирующимся логином: статус = {statusCode}, сообщение содержит '{errorMessage}'")
    private void checkDuplicateLoginResponse(Response response, int statusCode, String errorMessage) {
        response.then()
                .statusCode(statusCode)
                .body("message", containsString(errorMessage));
    }
}
