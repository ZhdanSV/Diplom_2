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

public class UserAuthorizationTest {

    /*
    POST https://stellarburgers.nomoreparties.site/api/auth/login
     */

    private String user;
    Random random = new Random();
    private String authToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = "{\"email\": \"testUser"+ random.nextInt(10000) +"@yandex.ru\",\n" +
                "\"password\": \"password\",\n" +
                "\"name\": \"Username\"}";
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

    @Step("Check authorisation user with invalid login-password pair")
    public void checkLoginUserWithInvalidLogPasPair() {
        given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .body(user)
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test  //логин под существующим пользователем,
    @DisplayName("Login user")
    public void loginUser() {
        Response response = creatingUser(user);
        authToken = getAuthToken(response);
        checkLoginUser();

    }

    @Test  //логин с неверным логином и паролем.
    @DisplayName("Create User with invalid login-password pair")
    public void loginUserWithInvalidLogPasPair() {
        user = "{\"email\": \"testUser"+ random.nextInt(1000) +"@yandex.ru\",\n" +
                "\"password\": \"123456\",\n" +
                "\"name\": \"Username\"}";
        Response response = creatingUser(user);
        authToken = getAuthToken(response);
        user = user.replace("123456", "111111");
        checkLoginUserWithInvalidLogPasPair();
    }

    @After
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
