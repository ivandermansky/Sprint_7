import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest extends BaseTestApi {
    private static final String GETORDERSENDPOINT = "/api/v1/orders";

    @Test
    @Feature("Заказы")
    @Description("Получение списка заказов для курьера с ID 741324")
    public void testGetOrdersForCourier() {
        // Создать объект с тестовыми данными
        GetOrdersTestData testData = new GetOrdersTestData(
                741324,           // courierId
                "",     // nearestStation
                0,              // number (количество заказов)
                0                // page (номер страницы)
        );

        given()
                .spec(requestSpec)
                .queryParam("courierId", testData.getCourierId())
                .queryParam("nearestStation", testData.getNearestStation())
                .queryParam("number", testData.getNumber())
                .queryParam("page", testData.getPage())
                .log().all()
                .when()
                .get(GETORDERSENDPOINT)
                .then()
                .log().ifError()
                .log().all()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("pageInfo.total", greaterThanOrEqualTo(0));
    }
}
