package users;

import models.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class OrderUser {
    private static final String BASE_URL = "https://qa-stellarburgers.education-services.ru";

    @Step("Создание заказа с авторизацией")
    public Response createOrder(Order order, String authToken) {
        return given()
                .header("Authorization", authToken)
                .contentType(JSON)
                .body(order)
                .when()
                .post(BASE_URL + "/api/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .contentType(JSON)
                .body(order)
                .when()
                .post(BASE_URL + "/api/orders");

    }

    @Step("Получение списка заказов")
    public Response getOrder(String authToken) {
        return given()
                .header("Authorization", authToken)
                .when()
                .get(BASE_URL + "/api/orders");
    }
    @Step("Получение списка заказов без авторизации")
    public Response getOrderWithoutAuth() {
        return given()
                .when()
                .get(BASE_URL + "/api/orders");
    }
}
