/*
Тест проверяет, что система возвращает ошибку если не передать какое-то из необходимых полей (логин или пароль).
Система должна возвращать 400 и {"message": "Недостаточно данных для входа"}.
При тесте, в котором проверяется авторизация без пароля возвращается ошибка 504, что свидетельствует о баге: сервер некорректно обрабатывает null
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
public class AuthWithMissingFieldsTest extends BaseTestApi {

    // Будут использованы те же данные, что и в AuthOnlyTest
    private static final String EXISTING_LOGIN = "182Blink182";
    private static final String EXISTING_PASSWORD = "182";

    @Test
    @Description("Проверка ошибки при отсутствии поля 'login' в запросе авторизации")
    public void testAuthWithoutLogin() {
        // Создать данные для авторизации без логина
        CourierTestData authData = new CourierTestData();
        authData.setPassword(EXISTING_PASSWORD);

        // Отправить запрос и проверить ответ через аннотированный метод
        Response response = sendAuthRequestWithMissingLogin(authData);
    }

    @Test
    @Description("Проверка ошибки при отсутствии поля 'password' в запросе авторизации")
    public void testAuthWithoutPassword() {
        // Создать данные для авторизации без пароля
        CourierTestData authData = new CourierTestData();
        authData.setLogin(EXISTING_LOGIN);

        // Отправить запрос и проверить ответ через аннотированный метод
        Response response = sendAuthRequestWithMissingPassword(authData);
    }

    @Step("Отправка запроса авторизации без поля 'login', пароль: '{password}'")
    private Response sendAuthRequestWithMissingLogin(CourierTestData authData) {
        return given()
                .spec(requestSpec)
                .body(authData)
                .log().all()
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .log().ifError()
                .log().all()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"))
                .extract().response();
    }

    @Step("Отправка запроса авторизации без поля 'password', логин: '{login}'")
    private Response sendAuthRequestWithMissingPassword(CourierTestData authData) {
        String login = authData.getLogin();
        return given()
                .spec(requestSpec)
                .body(authData)
                .log().all()
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .log().ifError()
                .log().all()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"))
                .extract().response();
    }
}
