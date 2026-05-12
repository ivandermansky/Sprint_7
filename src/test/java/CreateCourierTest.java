
// С помощью класса был создан курьер, который будет использоваться в нескольких последующих тестах

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
import static org.hamcrest.CoreMatchers.notNullValue;

@Feature("Курьеры")
public class CreateCourierTest {

    // Данные для создания курьера
    private static final String LOGIN = "182Blink182";
    private static final String PASSWORD = "182";
    private static final String FIRST_NAME = "Travis";

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
        if (courierIdToDelete > -1) {
            Response deleteResponse = courierSteps.deleteCourierById(courierIdToDelete);
            checkDeleteCourierResponse(deleteResponse);
        }
    }

    @Test
    @Description("Проверка создания курьера с заданным вручную логином")
    public void testCreateCourier() {
        String login = LOGIN;
        String password = PASSWORD;
        String firstName = FIRST_NAME;

        // Отправить запрос создания курьера через CourierSteps
        Response createResponse = courierSteps.createCourier(login, password, firstName);

        // Проверить ответ создания курьера в тестовом классе
        checkCreateCourierResponse(createResponse);

        // Получить ID курьера через авторизацию
        Response authResponse = courierSteps.authorizeCourier(login, password);

        // Проверить ответ авторизации в тестовом классе
        checkAuthResponse(authResponse);

        // Сохранить ID для автоматического удаления после теста
        courierIdToDelete = authResponse.jsonPath().getInt("id");
    }

    @Step("Проверка ответа создания курьера: статус-код 201, ok = true")
    private void checkCreateCourierResponse(Response response) {
        response.then()
                .log().ifError()      // логировать запрос и ответ, если статус указывает на ошибку (4xx, 5xx)
                .log().all()         // логировать весь ответ сервера (заголовки, тело, статус)
                .statusCode(201)     // проверить, что статус-код соответствует ожидаемому (201)
                .body("ok", equalTo(true)); // проверить, что поле ok равно true
    }

    @Step("Проверка ответа авторизации: статус-код 200, поле id присутствует")
    private void checkAuthResponse(Response response) {
        response.then()
                .log().all()           // логировать весь ответ
                .statusCode(200)        // проверить статус-код (200)
                .body("id", notNullValue()); // проверить наличие поля id
    }

    @Step("Проверка ответа удаления курьера: статус-код 200, ok = true")
    private static void checkDeleteCourierResponse(Response response) {
        response.then()
                .log().all()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
}
