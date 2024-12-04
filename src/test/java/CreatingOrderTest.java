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

public class CreatingOrderTest extends BaseURI{
    /*
    POST https://stellarburgers.nomoreparties.site/api/orders
     */

    private UserData user;
    private String email;
    private String password;
    private String name;
    Random random = new Random();
    private String authToken;
    private String ingHash;
    private String invalidHash;

    @Before
    public void setUp() {
        email = "testuser"+ random.nextInt(10000) +"@yandex.ru";
        password = "123456";
        name = "Username";
        user = new UserData(email, password, name);
        ingHash = getIngredients();
        invalidHash = "aaaaaaaaaaaaaa";

    }

    @Step("Create user")
    public Response creatingUser(UserData json) {
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
        login(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
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
        Response order = createOrderWithAuth(ingHash, authToken);
        checkResponseWithAuth(order);
    }

    @Test  //без авторизации
    @DisplayName("Create order without auth and with ingredients")
    public void createOrderWithoutAuthAndIngredients() {
        Response order = createOrderWithoutAuth(ingHash);
        checkResponseWithoutAuth(order);
    }


    @Test  //без ингредиентов
    @DisplayName("Create order without ingredients")
    public void createOrderWithoutIngredientsAndAuth() {
        Response order = createOrderWithoutAuth();
        checkResponseWithoutIngredients(order);
    }

    @Test  //с неверным хешем ингредиентов
    @DisplayName("Create order with invalid hash of ingredients")
    public void createOrderWithInvalidIngredientsHash() {
        Response order = createOrderWithoutAuth(invalidHash);
        checkResponseWithInvalidHash(order);
    }

    @After //ручка для удаления заказа отсутствует в документации
    public void delUser() {
        if (authToken!=null) {
            deleteUser(authToken)
                    .then()
                    .statusCode(202);
        }
    }
}
