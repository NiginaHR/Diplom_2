import Generate.UserGenerator;
import Models.Order;
import Models.User;
import Models.UserCreds;
import Users.OrderUser;
import Users.UserActions;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetOrderTest {

    private UserActions userActions;
    private OrderUser orderUser;
    private String authToken;


    @BeforeEach
    public void setUp() {
        userActions = new UserActions();
        orderUser = new OrderUser();

        User testUser = UserGenerator.randomUser();
        Response createResponse = userActions.create(testUser);

        createResponse.then().statusCode(200);
        UserCreds creds = new UserCreds()
                .setEmail(testUser.getEmail())
                .setPassword(testUser.getPassword());

        Response loginResponse = userActions.login(creds);
        loginResponse.then().statusCode(200);

        authToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @Description("Успешное получение заказа с авторизацией")
    public void getOrderWithAuth() {

        Order order = new Order(List.of("6acef97852135b001b21a32e"));
        Response createResponse = orderUser.createOrder(order, authToken);
        createResponse.then().statusCode(200);


        Response response = orderUser.getOrder(authToken);

        response.then().statusCode(200);
        assertTrue(response.jsonPath().getBoolean("success"));

        assertNotNull(response.jsonPath().getInt("total"));
        assertNotNull(response.jsonPath().getInt("totalToday"));

    }

    @Test
    @Description("Получение заказа без авторизации")
    public void getOrderWithoutAuth() {

        Order order = new Order(List.of("6acef97852135b001b21a32e"));
        Response createResponse = orderUser.createOrder(order, authToken);
        createResponse.then().statusCode(200);


        Response response = orderUser.getOrderWithoutAuth();


        response.then().statusCode(401);
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("You should be authorised", response.jsonPath().getString("message"));

    }
    @AfterEach
    public void tearDown() {
        if (authToken != null) {
            userActions.deleteUser(authToken)
                    .then()
                    .statusCode(202);
        }
    }
}



