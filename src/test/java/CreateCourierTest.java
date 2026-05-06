import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest extends BaseTestApi {
  private static final String CREATEUSERENDPOINT = "/api/v1/courier";


  @Test
  @Feature("Курьеры")
  @Description("Проверка создания курьера с заданным вручную логином")
  public void testCreateCourier() {
// Данный логин (dvgfdsgwfswa) уже ранее был использован для регистрации, так что будет ошибка
// Если удалить логин или пароль, то появляется ошибка "Недостаточно данных для создания учётной записи"
    CourierTestData courierData = new CourierTestData("dvgfdsgwfswa", "password123", "Пётр");

    Response response = given()
            .spec(requestSpec) // использовать из BaseTestApi
            .body(courierData)
            .log().all()
            .when()
            .post(CREATEUSERENDPOINT)
            .then()
            .log().ifError() // логировать ответ при ошибке
            .extract().response();

    response.then()
            .statusCode(201)
            .body("ok", equalTo(true));
  }
}