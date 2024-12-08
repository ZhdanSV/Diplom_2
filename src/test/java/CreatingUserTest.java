import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatingUserTest extends BaseURIAndAPIs {
    /*
    POST https://stellarburgers.nomoreparties.site/api/auth/register
     */
    private UserData user;
    private String email;
    private String password;
    private String name;
    private String authToken;
    Random random = new Random();


    @Before
    public void setUp() {
        email = "testuser"+ random.nextInt(10000) +"@yandex.ru";
        password = "123456";
        name = "Username";
        user = new UserData(email,password,name);
    }


    @Step("check response for duplicate creating user")
    public void checkResponseDuplicateCreating(Response response) {
        response
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("check Response Without Pass Or Email")
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
        Response response = creatingUser(user);
        authToken = getAuthToken(response);
    }

    @Test  //создать пользователя, который уже зарегистрирован;
    @DisplayName("Create duplicate User")
    public void creatingDuplicateUser() {
        Response response = creatingUser(user);
        authToken = getAuthToken(response);
        Response secondResponse = creatingUser(user);
        checkResponseDuplicateCreating(secondResponse);
    }

    @Test  //создать пользователя и не заполнить одно из обязательных полей.
    @DisplayName("Create User without password")
    public void creatingUserWithoutField() {
        UserData invalidUser = new UserData(email,"",name);
        Response response = creatingUser(invalidUser);
        checkResponseWithoutPassOrEmail(response);
    }

    @After
    public void delUser() {
        if (authToken!=null) {
            deleteUser(authToken);
        }

    }

}
