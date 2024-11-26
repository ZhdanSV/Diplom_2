import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatingOrderTest {
    /*
    POST https://stellarburgers.nomoreparties.site/api/orders
     */

    private String user;
    Random random = new Random();
    private String authToken;
    private String ingredients;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = "{\"email\": \"testuser"+ random.nextInt(10000) +"@yandex.ru\",\n" +
                "\"password\": \"password\",\n" +
                "\"name\": \"Username\"}";
        ingredients = "{\n" +
                "\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]\n" +
                "}\n";
    }

    @Step("Create user")
    public Response creatingUser(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/auth/register");
    }

    @Step("Get auth token")
    public String getAuthToken(Response response) {
        return response
                .then()
                .extract()
                .path("accessToken")
                .toString()
                .replace("Bearer ", "");
    }

    @Step("Check authorisation user")
    public void checkLoginUser() {
        given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .body(user)
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Create user with auth")
    public Response createOrderWithAuth(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .auth().oauth2(authToken)
                .post("/api/orders");
    }

    @Step("Create order without auth")
    public Response createOrderWithoutAuth(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
//                .auth().oauth2(authToken)
                .post("/api/orders");
    }

    @Step("Check response with auth")
    public void checkResponseWithAuth(Response response) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order", notNullValue())
                .body("name", notNullValue());
    }

    @Step("Check response without auth and ingredients")
    public void checkResponseWithoutAuth(Response response) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order", notNullValue())
                .body("name", notNullValue());
    }

    @Step("Check response without ingredients")
    public void checkResponseWithoutIngredients(Response response) {
        response
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Check response with invalid hash")
    public void checkResponseWithInvalidHash(Response response) {
        response
                .then()
                .statusCode(500);
    }


    /*
    Создание заказа:
     */
    @Test  //с авторизацией и ингридиентами
    @DisplayName("Create order with auth and ingredients")
    public void createOrderWithAuthAndIngredients() {
        Response userResponse = creatingUser(user);
        authToken = getAuthToken(userResponse);
        checkLoginUser();
        Response order = createOrderWithAuth(ingredients);
        checkResponseWithAuth(order);
    }

    @Test  //без авторизации
    @DisplayName("Create order without auth and with ingredients")
    public void createOrderWithoutAuthAndIngredients() {
        Response order = createOrderWithoutAuth(ingredients);
        checkResponseWithoutAuth(order);
    }


    @Test  //без ингредиентов
    @DisplayName("Create order without ingredients")
    public void createOrderWithoutIngredientsAndAuth() {
        ingredients = "{}";
        Response order = createOrderWithoutAuth(ingredients);
        checkResponseWithoutIngredients(order);
    }

    @Test  //с неверным хешем ингредиентов
    @DisplayName("Create order with invalid hash of ingredients")
    public void createOrderWithInvalidIngredientsHash() {
        ingredients = "{\"ingredients\": [\"60d3b41abdacab0026a733\",\"609646e4dc916e00276b2\"]}";
        Response order = createOrderWithoutAuth(ingredients);
        checkResponseWithInvalidHash(order);
    }

    @After //ручка для удаления заказа отсутствует в документации
    public void delUser() {
        try {
            given()
                    .header("Content-type", "application/json")
                    .auth().oauth2(authToken)
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        } catch (IllegalArgumentException illegalArgumentException) {
            return;
        }
    }
}
