
// Тест используется для получения списка заказов курьера по id курьера. Здесь есть баг - система возвращает ответ не в ожидаемом формате

package test;

import Steps.OrderSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.response.Response;
import models.BaseTestApi;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Заказы")
public class GetOrdersTest extends BaseTestApi {

    // Создать экземпляр OrderSteps
    private static OrderSteps orderSteps;

    @BeforeClass
    public static void setup() {
        BaseTestApi.setup();
        orderSteps = new OrderSteps(requestSpec);
    }

    @Test
    @Description("Получение списка заказов для курьера с ID 741324")
    public void testGetOrdersForCourier() {
        // Вызвать метод из OrderSteps — получаем Response
        Response response = orderSteps.getOrdersForCourierWithDefaultData();

        // Проверить ответ в тестовом классе — вся логика проверок здесь
        checkGetOrdersResponse(response);
    }

    @Step("Проверка ответа получения списка заказов: статус-код 200, наличие заказов")
    private void checkGetOrdersResponse(Response response) {
        response.then()
                .log().ifError()
                .log().all()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("pageInfo.total", greaterThanOrEqualTo(0));
    }
}
