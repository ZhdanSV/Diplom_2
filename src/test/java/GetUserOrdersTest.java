import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetUserOrdersTest extends BaseURIAndAPIs {
    /*
    GET https://stellarburgers.nomoreparties.site/api/orders
     */

    private UserData user;
    private String email;
    private String password;
    private String name;
    Random random = new Random();
    private String authToken;
    private String ingHash;

    @Before
    public void setUp() {
        email = "testuser"+ random.nextInt(10000) +"@yandex.ru";
        password = "123456";
        name = "Username";
        user = new UserData(email, password, name);
        ingHash = getIngredients();
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

    @Step("Check authorisation of user ")
    public void checkLoginUser() {
        login(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Check get user order with authorisation")
    public void checkGetUserOrdersWithAuth() {
        getUserOrders(authToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Step("Check get user orders without auth")
    public void checkGetUserOrdersWithoutAuth() {
        getUserOrders()
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    /*
    Получение заказов пользователя
     */
    @Test  //авторизованный пользователь
    @DisplayName("Get authorized user orders")
    public void getAuthUserOrders() {
        authToken = getAuthToken(creatingUser(user));
        checkLoginUser();
        createOrderWithAuth(ingHash,authToken);
        checkGetUserOrdersWithAuth();

    }

    @Test  //неавторизованный пользователь
    @DisplayName("Get not authorized user orders")
    public void getNonAuthUserOrders() {
        authToken = getAuthToken(creatingUser(user));
        checkLoginUser();
        createOrderWithAuth(ingHash, authToken);
        checkGetUserOrdersWithoutAuth();
    }

    @After //ручка для удаления заказа отсутствует в документации
    public void delUser() {
        if (authToken!=null) {
            deleteUser(authToken);
        }
    }

}
