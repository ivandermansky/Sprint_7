// Тест проверяет, что при авторизации курьера возвращается id курьера


import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CourierTestData;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Авторизация курьеров")
public class AuthIdTest extends BaseTestApi {

    // Данные существующего пользователя
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    @Test
    @Description("Проверка успешной авторизации существующего курьера и возврата корректного ID")
    public void testSuccessfulAuthWithIdValidation() {
        // Формировать данные для авторизации
        CourierTestData authData = new CourierTestData();
        authData.setLogin(EXISTING_LOGIN);
        authData.setPassword(EXISTING_PASSWORD);

        // Отправить запрос на авторизацию
        Response response = sendAuthRequest(authData);

        // Дополнительная проверка: извлечь ID и вывести в лог
        Integer userId = response.jsonPath().get("id");
        System.out.println("Полученный ID пользователя: " + userId);

        // Убедиться, что ID действительно число и больше нуля (дополнительная проверка)
        validateUserId(userId);
    }

    @Step("Отправка запроса авторизации с данными: login={login}, password={password}")
    private Response sendAuthRequest(CourierTestData authData) {
        return given()
                .spec(requestSpec)
                .body(authData)
                .log().all() // логировать отправляемые данные и ответ
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .log().all()
                .statusCode(200)
                // Проверить, что ID существует
                .body("id", notNullValue())
                // Проверить, что ID — число
                .body("id", is(greaterThan(-1)))
                // Дополнительно проверить, что ID больше нуля
                .body("id", greaterThan(0))
                .extract().response();
    }

    @Step("Валидация ID пользователя: {userId}")
    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new AssertionError("ID пользователя не должен быть null");
        }
        if (userId <= 0) {
            throw new AssertionError("ID пользователя должен быть больше нуля, но получен: " + userId);
        }
    }
}
