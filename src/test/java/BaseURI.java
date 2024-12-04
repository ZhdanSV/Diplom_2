import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BaseURI {
    public BaseURI() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    public Response changeData(UserData data, String authToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .body(data)
                .patch("/api/auth/user");
    }

    public Response changeData(UserData data) {
        return given()
                .header("Content-type", "application/json")
                .body(data)
                .patch("/api/auth/user");
    }

    public Response login(UserData user) {
        return given()
                .header("Content-type", "application/json")

                .body(user)
                .post("/api/auth/login");
    }

    public Response creatingUser(UserData user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/register");
    }

    public Response deleteUser(String authToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .delete("/api/auth/user");
    }

    public String getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .get("api/ingredients")
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
                .post("/api/orders");
    }

    public Response createOrderWithoutAuth(String ingHash) {
        String ingredients = "{\n" +
                "\"ingredients\": [\""+ingHash+"\"]\n" +
                "}\n";
        return given()
                .header("Content-type", "application/json")
                .body(ingredients)
                .post("/api/orders");
    }

    public Response createOrderWithoutAuth() {
        return given()
                .header("Content-type", "application/json")
                .post("/api/orders");
    }

    public Response getUserOrders() {
        return given()
                .header("Content-type", "application/json")
                .get("/api/orders");
    }

    public Response getUserOrders(String authToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(authToken)
                .get("/api/orders");
    }

}
