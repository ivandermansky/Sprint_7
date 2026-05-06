import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthCourierTest extends BaseTestApi {
    private static final String AUTHUSERENDPOINT = "/api/v1/courier/login";

    // Данные логин и пароль являются валидными. Если указать неправильные логин или пароль, система возвращает ошибку
    private static final String LOGIN = "dvgfdsgwfswa";
    private static final String PASSWORD = "password123";

    @Test
    @Feature("Курьеры")
    @Description("Авторизация курьера с корректными учётными данными")
    public void testCourierAuth() {
        given()
                .spec(requestSpec) // использовать спецификацию из BaseTestApi
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParams("login", LOGIN, "password", PASSWORD)
                .log().all()
                .when()
                .post(AUTHUSERENDPOINT)
                .then()
                .log().all() // Логировать всё для получения id
                .statusCode(200)
                .body("id", notNullValue())
                .extract().response();
    }
}
