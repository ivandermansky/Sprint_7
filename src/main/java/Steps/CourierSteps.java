package Steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;
import models.CourierTestData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;

public class CourierSteps {

    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    public static final String CREATE_COURIER_ENDPOINT = "/api/v1/courier";
    public static final String AUTH_COURIER_ENDPOINT = "/api/v1/courier/login";
    public static final String DELETE_COURIER_ENDPOINT = "/api/v1/courier/{courierId}";
    public static RequestSpecification requestSpec;

    protected int courierIdToDelete = -1;

    // Конструктор для гарантированной инициализации
    public CourierSteps() {
        if (requestSpec == null) {
            setup();
        }
    }

    @BeforeClass
    public static void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json; charset=UTF-8")
                .build();
    }



    // === ОСНОВНЫЕ МЕТОДЫ РАБОТЫ С КУРЬЕРАМИ ===

    /*
     - Создание курьера с указанными данными
     */
    @Step("Создание курьера: логин = {login}")
    public Response createCourier(String login, String password, String firstName) {
        CourierTestData courierData = new CourierTestData(login, password, firstName);
        return createCourierWithData(courierData);
    }

    /*
     - Создание курьера с использованием объекта CourierTestData
     */
    @Step("Создание курьера с данными: {courierData}")
    public Response createCourierWithData(CourierTestData courierData) {
        return given()
                .spec(requestSpec)
                .body(courierData)
                .log().all()
                .when()
                .post(CREATE_COURIER_ENDPOINT);
    }

    /*
     - Создание курьера без поля login
     */
    @Step("Создание курьера без поля 'login', пароль: '{password}', имя: '{firstName}'")
    public Response createCourierWithoutLogin(String password, String firstName) {
        CourierTestData courierData = new CourierTestData();
        courierData.setPassword(password);
        courierData.setFirstName(firstName);
        // login остаётся null
        return createCourierWithData(courierData);
    }

    /*
     - Создание курьера без поля password
     */
    @Step("Создание курьера без поля 'password', логин: '{login}', имя: '{firstName}'")
    public Response createCourierWithoutPassword(String login, String firstName) {
        CourierTestData courierData = new CourierTestData();
        courierData.setLogin(login);
        courierData.setFirstName(firstName);
        // password остаётся null
        return createCourierWithData(courierData);
    }

    /*
     - Авторизация курьера с указанными учётными данными
     */
    @Step("Авторизация курьера: логин = {login}")
    public Response authorizeCourier(String login, String password) {
        CourierTestData authData = new CourierTestData();
        authData.setLogin(login);
        authData.setPassword(password);
        return sendAuthRequestWithData(authData);
    }

    /*
     - Удаление курьера по ID
     */
    @Step("Удаление курьера по ID: {courierId}")
    public Response deleteCourierById(int courierId) {
        return given()
                .spec(requestSpec)
                .pathParam("courierId", courierId)
                .log().all()
                .when()
                .delete(DELETE_COURIER_ENDPOINT);
    }

    /*
     - Авторизация через form-params
     */
    @Step("Авторизация через form-data: логин = {login}")
    public Response authCourierForm(String login, String password) {
        return given()
                .spec(requestSpec)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParams("login", login, "password", password)
                .log().all()
                .when()
                .post(AUTH_COURIER_ENDPOINT);
    }



    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ АВТОРИЗАЦИИ ===

    /*
     - Отправка запроса авторизации с данными курьера
     */
    @Step("Отправка запроса авторизации с данными: login={login}, password={password}")
    public Response sendAuthRequest(String login, String password) {
        CourierTestData authData = new CourierTestData();
        authData.setLogin(login);
        authData.setPassword(password);
        return sendAuthRequestWithData(authData);
    }

    /*
     - Отправка запроса авторизации с объектом CourierTestData
     */
    @Step("Отправка запроса авторизации с данными курьера")
    public Response sendAuthRequestWithData(CourierTestData authData) {
        return given()
                .spec(requestSpec)
                .body(authData)
                .log().all()
                .when()
                .post(AUTH_COURIER_ENDPOINT);
    }

    /*
     - Отправка запроса авторизации без поля login
     */
    @Step("Отправка запроса авторизации без поля 'login', пароль: '{password}'")
    public Response sendAuthRequestWithoutLogin(String password) {
        CourierTestData authData = new CourierTestData();
        authData.setPassword(password);
        // login остаётся null
        return sendAuthRequestWithData(authData);
    }

    /*
     - Отправка запроса авторизации без поля password
     */
    @Step("Отправка запроса авторизации без поля 'password', логин: '{login}'")
    public Response sendAuthRequestWithoutPassword(String login) {
        CourierTestData authData = new CourierTestData();
        authData.setLogin(login);
        // password остаётся null
        return sendAuthRequestWithData(authData);
    }

    /*
     - Отправка запроса авторизации с несуществующим пользователем
     */
    @Step("Отправка запроса авторизации с несуществующим логином '{login}' и паролем '{password}'")
    public Response sendAuthRequestWithNonExistingUser(String login, String password) {
        CourierTestData authData = new CourierTestData();
        authData.setLogin(login);
        authData.setPassword(password);
        return sendAuthRequestWithData(authData);
    }


    // === МЕТОДЫ ПРОВЕРКИ ОТВЕТОВ ===

    /*
     - Проверка успешного ответа авторизации
     - Ожидается статус 200 и наличие поля id в ответе
     */
    @Step("Проверка успешного ответа авторизации: ожидается статус 200 и поле 'id'")
    public void checkSuccessfulAuthResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .log().ifError();
    }

    /*
     Проверка ответа при ошибке авторизации
      response — полученный ответ
      statusCode — ожидаемый HTTP статус-код (например, 404)
      errorMessage — подстрока, которая должна содержаться в поле message тела ответа
     */
    @Step("Проверка ответа при ошибке авторизации: статус = {statusCode}, сообщение содержит '{errorMessage}'")
    public void checkAuthErrorResponse(Response response, int statusCode, String errorMessage) {
        response.then()
                .statusCode(statusCode)
                .body("message", containsString(errorMessage))
                .log().ifError();
    }

    
    // === МЕТОДЫ ОЧИСТКИ ДАННЫХ ===

    /*
     - Очистка данных: удаление курьера по ID
     - Используется для очистки тестовых данных после тестов
     */
    @Step("Очистка данных: удаление курьера с ID = {courierId}")
    public void cleanUpCourier(int courierId) {
        if (courierId != -1) {
            deleteCourierById(courierId);
        }
    }

    /*
     - Установка ID курьера для последующего удаления
     */
    public void setCourierIdForCleanup(int courierId) {
        this.courierIdToDelete = courierId;
    }

    /*
     - Получение ID курьера для удаления
     */
    public int getCourierIdForCleanup() {
        return courierIdToDelete;
    }
}
