import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import static org.hamcrest.CoreMatchers.equalTo;


public class UserAuthorizationTest extends BaseURIAndAPIs {

    /*
    POST https://stellarburgers.nomoreparties.site/api/auth/login
     */

    private UserData user;
    private UserData invalidUser;
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
        invalidUser = new UserData(email, password+"123", name);
    }


    @Step("Check authorisation user")
    public void checkLoginUser() {
        login(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Check authorisation user with invalid login-password pair")
    public void checkLoginUserWithInvalidLogPasPair() {
       login(invalidUser)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test  //логин под существующим пользователем,
    @DisplayName("Login user")
    public void loginUser() {
        authToken = getAuthToken(creatingUser(user));
        checkLoginUser();

    }

    @Test  //логин с неверным логином и паролем.
    @DisplayName("Create User with invalid login-password pair")
    public void loginUserWithInvalidLogPasPair() {
        Response response = creatingUser(user);
        authToken = getAuthToken(response);
        checkLoginUserWithInvalidLogPasPair();
    }

    @After
    public void delUser() {
        if (authToken!=null) {
            deleteUser(authToken);
        }
    }
}
