import io.qameta.allure.Step;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.restassured.response.Response;
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
    private static final String ORDERENDPOINT = "/api/v1/orders";

    private final String testDescription;
    private final OrderTestData orderData;

    public OrderTest(String testDescription, OrderTestData orderData) {
        this.testDescription = testDescription;
        this.orderData = orderData;
    }

    // Параметризованный тест с тремя наборами тестовых данных
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
                .spec(requestSpec) // использовать из BaseTestApi
                .body(orderData)
                .log().all()
                .when()
                .post(ORDERENDPOINT)
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
