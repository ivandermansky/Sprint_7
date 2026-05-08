
// С помощью класса был создан курьер, который будет использоваться в нескольких последующих тестах


import Steps.CourierSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest extends CourierSteps {

    @Test
    @Feature("Курьеры")
    @Description("Проверка создания курьера с заданным вручную логином")
    public void testCreateCourier() {
        String login = "182Blink182";
        String password = "182";
        String firstName = "Travis"; // добавляем имя курьера

        Response createResponse = createCourier(login, password, firstName);
        checkResponse(createResponse, 201, true);

        // Сохранить ID для автоматического удаления после теста
        courierIdToDelete = authorizeCourier(login, password); // передаём login и password
    }

    @Step("Проверка ответа: статус-код = {statusCode}, ok = {okValue}")
    private void checkResponse(Response response, int statusCode, boolean okValue) {
        response.then()
                .statusCode(statusCode)
                .body("ok", equalTo(okValue));
    }
}
