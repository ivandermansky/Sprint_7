package Steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.GetOrdersTestData;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    public static final String GET_ORDERS_ENDPOINT = "/api/v1/orders";

    private RequestSpecification requestSpec;

    // Конструктор, принимающий requestSpec
    public OrderSteps(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    // Метод для получения списка заказов для курьера с фиксированными тестовыми данными
     // Создаёт тестовые данные внутри метода, отправляет запрос и возвращает Response

    @Step("Получение списка заказов для курьера с ID 742515")
    public Response getOrdersForCourierWithDefaultData() {
        // Создать объект с тестовыми данными внутри метода
        GetOrdersTestData testData = new GetOrdersTestData(
                742515,           // courierId
                "",               // nearestStation
                0,              // number (количество заказов)
                0               // page (номер страницы)
        );

        return given()
                .spec(requestSpec)
                .queryParam("courierId", testData.getCourierId())
                .queryParam("nearestStation", testData.getNearestStation())
                .queryParam("number", testData.getNumber())
                .queryParam("page", testData.getPage())
                .log().all()
                .when()
                .get(GET_ORDERS_ENDPOINT);
    }
}
