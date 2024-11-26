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

public class CreatingUserTest {
    /*
    POST https://stellarburgers.nomoreparties.site/api/auth/register
     */

    private String authToken;
    Random random = new Random();
    private String user;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = "{\"email\": \"test"+ random.nextInt(1000) +"@yandex.ru\",\n" +
            "\"password\": \"password\",\n" +
            "\"name\": \"Username\"}";
    }

    @Step("")
    public Response createUserRequest(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/auth/register");
    }

    @Step("")
    public String getAuthToken(Response response) {
        return response
                .then()
                .statusCode(200)
                .body("success", notNullValue())
                .extract()
                .path("accessToken")
                .toString()
                .replace("Bearer ", "");
    }

    @Step("")
    public void checkResponseDuplicateCreating(Response response) {
        response
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("")
    public void checkResponseWithoutPassOrEmail(Response response) {
        response
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }



    @Test //создать уникального пользователя;
    @DisplayName("Create User")
    public void creatingUser() {
        Response response = createUserRequest(user);
        authToken = getAuthToken(response);

    }

    @Test  //создать пользователя, который уже зарегистрирован;
    @DisplayName("Create duplicate User")
    public void creatingDuplicateUser() {
        Response response = createUserRequest(user);
        authToken = getAuthToken(response);
        Response secondResponse = createUserRequest(user);
        checkResponseDuplicateCreating(secondResponse);
    }

    @Test  //создать пользователя и не заполнить одно из обязательных полей.
    @DisplayName("Create User without password")
    public void creatingUserWithoutField() {
        String invalidUser = "{\"email\": \"test"+ random.nextInt(1000) +"@yandex.ru\",\n" +

                "\"name\": \"Username\"}";
        Response response = createUserRequest(invalidUser);
        checkResponseWithoutPassOrEmail(response);
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
