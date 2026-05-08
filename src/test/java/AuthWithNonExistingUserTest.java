/*
Тест проверяет, что если авторизоваться под несуществующим курьером, запрос возвращает ошибку.
Для этого используется подбор рандомного логина с помощью currentTimeMillis()
*/

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CourierTestData;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@Feature("Авторизация курьеров")
public class AuthWithNonExistingUserTest extends BaseTestApi {

    // Данные существующего пользователя (для сравнения)
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    @Test
    @Description("Проверка ошибки при авторизации с несуществующим логином")
    public void testAuthWithNonExistingLogin() {
        // Создать рандомный логин на основе currentTimeMillis()
        String randomLogin = "nonexistent_" + System.currentTimeMillis();
        String password = "any_password"; // пароль может быть любым

        // Формировать данные для авторизации с несуществующим логином
        CourierTestData authData = new CourierTestData();
        authData.setLogin(randomLogin);
        authData.setPassword(password);

        System.out.println("Тестируемый логин (несуществующий): " + randomLogin);

        // Отправить запрос на авторизацию через аннотированный метод
        Response response = sendAuthRequestWithNonExistingUser(authData, randomLogin);
    }

    @Step("Отправка запроса авторизации с несуществующим логином '{login}' и паролем '{password}'")
    private Response sendAuthRequestWithNonExistingUser(CourierTestData authData, String login) {
        return given()
                .spec(requestSpec)
                .body(authData)
                .log().all()
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .log().ifError()
                .log().all()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"))
                .extract().response();
    }
}
