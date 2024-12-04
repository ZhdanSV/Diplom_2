import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserDataTest extends BaseURI {
    /*
    PATCH https://stellarburgers.nomoreparties.site/api/auth/user
     */

    private UserData user;
    private UserData newData;
    private String email;
    private String password;
    private String name;
    Random random = new Random();
    private String authToken;

    @Before
    public void setUp() {
        email = "testuser"+ random.nextInt(10000) +"@yandex.ru";
        password = "123456";
        name = "Username";
        user = new UserData(email, password, name);
        newData = new UserData("a"+email, "aaa"+password, "Naruto");
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


    @Step("Change user data")
    public void changeUserData() {
        changeData(newData, authToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newData.getName()))
                .body("user.email", equalTo(newData.getEmail()));
    }

    @Step("Check change user data without authorisation")
    public void checkChangeUserDataWithoutAuth() {
        changeData(newData)
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
        login(user);
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
        if (authToken!=null) {
            deleteUser(authToken)
                    .then()
                    .statusCode(202);
        }
    }

}
