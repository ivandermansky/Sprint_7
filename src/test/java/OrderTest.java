

/* Параметризованный тест для проверки создания заказа. Содержит четыре набора данных:
        - Первый делает заказ с цветом BLACK;
        - Второй делает заказ с цветом GREY;
        - В третьем заказе цвет не указан;
        - В четвёртомм делается заказ сразу для двух цветов
        Тело ответа содержит `track`
*/


import io.qameta.allure.Step;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import models.BaseTestApi;
import models.OrderTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
@Feature("Заказы")
public class OrderTest extends BaseTestApi {
    private static final String ORDER_ENDPOINT = "/api/v1/orders";

    private final String testDescription;
    private final OrderTestData orderData;

    public OrderTest(String testDescription, OrderTestData orderData) {
        this.testDescription = testDescription;
        this.orderData = orderData;
    }

    // Параметризованный тест с четырьмя наборами тестовых данных. Четвёртый будет проверять передачу 2 цветов сразу
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> provideTestData() {
        return Arrays.asList(
                new Object[] {
                        "Тестовые данные для заказа: Иван Иванов",
                        new OrderTestData("Иван", "Иванов", "Красная 10", 4, "+7 888 888 88 88", 5, "2026-06-06", "Курьер должен позвонить", List.of("BLACK"))
                },
                new Object[] {
                        "Тестовые данные для заказа: Алла Сидорова",
                        new OrderTestData("Алла", "Сидорова", "Северная 22", 3, "+7 888 777 88 88", 4, "2026-06-07", "Не звоните мне", List.of("GREY"))
                },
                new Object[] {
                        "Тестовые данные для заказа: Виолетта Петрова",
                        new OrderTestData("Виолетта", "Петрова", "Уральская 44", 3, "+7 888 777 88 77", 3, "2026-06-07", "Хочу самокат", List.of())
                },
                new Object[] {
                        "Тестовые данные для заказа: Александр Семёнов",
                        new OrderTestData("Александр", "Семёнов", "Севастопольская 100", 5, "+ 7 123 444 33 88", 2, "2026-07-07", "Я просто люблю самокаты", List.of("BLACK", "GREY"))
                }
        );
    }

    @Test
    @Description("Создание заказа с различными наборами данных")
    public void testCreateOrder() {
        Response response = createOrder(orderData);
        validateOrderCreation(response);
    }

    @Step("Создание заказа: {testDescription}")
    protected Response createOrder(OrderTestData orderData) {
        return given()
                .spec(requestSpec) // использовать из models.BaseTestApi
                .body(orderData)
                .log().all()
                .when()
                .post(ORDER_ENDPOINT)
                .then()
                .log().ifError()
                .extract().response();
    }

    @Step("Проверка успешного создания заказа")
    protected void validateOrderCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
