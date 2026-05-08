/*
Тест проверяет успешную авторизацию заранее созданного курьера.
Логин и пароль данного курьера будут далее использоваться:
- для теста на возвращение ошибки при неправильном логине и/или пароле
- для теста, который проверяет возвращение id при авторизации
*/

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CourierTestData;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Авторизация курьеров")
public class AuthOnlyTest extends BaseTestApi {

    // Данные существующего пользователя
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    @Test
    @Description("Проверка успешной авторизации существующего курьера")
    public void testSuccessfulAuth() {
        // Формировать данные для авторизации
        CourierTestData authData = new CourierTestData();
        authData.setLogin(EXISTING_LOGIN);
        authData.setPassword(EXISTING_PASSWORD);

        // Отправить запрос на авторизацию через аннотированный метод
        Response response = sendAuthRequest(authData);
    }

    @Step("Отправка запроса авторизации с логином '{login}' и паролем '{password}'")
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
                .body("id", notNullValue())
                .extract().response();
    }
}
