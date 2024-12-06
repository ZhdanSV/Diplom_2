import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BaseURIAndAPIs {
    private final String CHANGE_USER_DATA = "/api/auth/user";
    private final String LOGIN_USER = "/api/auth/login";
    private final String USER_REGISTRATION = "/api/auth/register";
    private final String DELETE_USER = "/api/auth/user";
    private final String GET_INGREDIENTS = "api/ingredients";
    private final String ORDERS = "/api/orders";

    public BaseURIAndAPIs() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    public Response changeData(UserData data, String authToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .body(data)
                .patch(CHANGE_USER_DATA);
    }

    public Response changeData(UserData data) {
        return given()
                .header("Content-type", "application/json")
                .body(data)
                .patch(CHANGE_USER_DATA);
    }

    public Response login(UserData user) {
        return given()
                .header("Content-type", "application/json")

                .body(user)
                .post(LOGIN_USER);
    }

    public Response creatingUser(UserData user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(USER_REGISTRATION);
    }

    public Response deleteUser(String authToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .delete(DELETE_USER);
    }

    public String getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .get(GET_INGREDIENTS)
                .then()
                .extract()
                .path("data[1]._id")
                .toString();
    }

    public Response createOrderWithAuth(String ingHash, String authToken) {
        String ingredients = "{\n" +
                "\"ingredients\": [\""+ingHash+"\"]\n" +
                "}\n";
        return given()
                .header("Content-type", "application/json")
                .body(ingredients)
                .auth().oauth2(authToken)
                .post(ORDERS);
    }

    public Response createOrderWithoutAuth(String ingHash) {
        String ingredients = "{\n" +
                "\"ingredients\": [\""+ingHash+"\"]\n" +
                "}\n";
        return given()
                .header("Content-type", "application/json")
                .body(ingredients)
                .post(ORDERS);
    }

    public Response createOrderWithoutAuth() {
        return given()
                .header("Content-type", "application/json")
                .post(ORDERS);
    }

    public Response getUserOrders() {
        return given()
                .header("Content-type", "application/json")
                .get(ORDERS);
    }

    public Response getUserOrders(String authToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .get(ORDERS);
    }

}
