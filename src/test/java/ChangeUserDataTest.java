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

public class ChangeUserDataTest {
    /*
    PATCH https://stellarburgers.nomoreparties.site/api/auth/user
     */

    private String user;
    private String newData;
    Random random = new Random();
    private String authToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = "{\"email\": \"test"+ random.nextInt(1000) +"@yandex.ru\",\n" +
                "\"password\": \"password\",\n" +
                "\"name\": \"Username\"}";
        newData = "{\"email\": \"narutoshippuuden1001@yandex.ru\",\n" +
                "\"password\": \"password1\",\n" +
                "\"name\": \"Naruto\"}";
    }

    @Step("")
    public Response creatingUser(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/auth/register");
    }

    @Step("")
    public String getAuthToken(Response response) {
        return response
                .then()
                .extract()
                .path("accessToken")
                .toString()
                .replace("Bearer ", "");
    }

    @Step("")
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

    @Step("")
    public void changeUserData() {
        given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .body(newData)
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo("Naruto"))
                .body("user.email", equalTo("narutoshippuuden1001@yandex.ru"));
    }

    @Step("")
    public void checkChangeUserDataWithoutAuth() {
        given()
                .header("Content-type", "application/json")
//                .auth().oauth2(authToken)
                .body(newData)
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test  //Изменение данных пользователя с авторизацией
    @DisplayName("Change User data with auth")
    public void changeUserDataWithAuth() {
        Response response = creatingUser(user);
        authToken = getAuthToken(response);
        checkLoginUser();
        changeUserData();
    }

    @Test //Изменение данных пользователя без авторизации
    @DisplayName("Change User data without auth")
    public void changeUserDataWithoutAuth() {
        Response response = creatingUser(user);
        authToken = getAuthToken(response);
        checkChangeUserDataWithoutAuth();
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
