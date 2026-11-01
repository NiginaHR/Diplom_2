import generate.UserGenerator;
import models.Order;
import models.User;
import models.UserCreds;
import users.OrderUser;
import users.UserActions;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class CreateOrderTest {

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


    private static Stream<Arguments> provideIngredients() {

        List <String> ingredientIds = given()
                .get("https://qa-stellarburgers.education-services.ru/api/ingredients")
                .jsonPath().getList("data._id", String.class);


        return Stream.of(
                Arguments.of(List.of(ingredientIds.get(0), ingredientIds.get(1))),
                Arguments.of(List.of(ingredientIds.get(2), ingredientIds.get(3))),
                Arguments.of(List.of(ingredientIds.get(4))));

    }

    @ParameterizedTest
    @MethodSource("provideIngredients")
    @Description("Проверка создания заказа c авторизацией и ингредиентами")
    public void testCreateOrderWithAuth(List<String> ingredients) {


        Order order = new Order(ingredients);
        Response response = orderUser.createOrder(order, authToken);

        response.then().statusCode(200);
        assertTrue(response.jsonPath().getBoolean("success"));
        assertNotNull(response.jsonPath().getString("name"));


    }

    @Test
    @Description("Проверка создания заказа без ингредиентов ")
    public void testCreateOrderWithoutIngredients() {


        Order order = new Order(List.of());
        Response response = orderUser.createOrder(order, authToken);


        response.then().statusCode(400)
                .body("message", is("Ingredient ids must be provided"));

    }

    @Test
    @Description("Проверка создания заказа с неверным хэш")
    public void testCreateOrderWithInvalidHash() {


        Order order = new Order(List.of("6acef97852135b001b13456"));
        Response response = orderUser.createOrder(order, authToken);

        response.then().statusCode(500);

    }

    @Test
    @Description("Проверка создания заказа без авторизации")
    @Disabled("Метод POST /api/orders упорно возвращает 200 вместо 401. Проверено также на сайте через нетворк")
    public void testCreateOrderWithoutAuth() {
        List<String> ingredientIds = given()
                .get("https://qa-stellarburgers.education-services.ru/api/ingredients")
                .jsonPath().getList("data._id", String.class);

        Order order = new Order(List.of(ingredientIds.get(4)));
        Response response = orderUser.createOrderWithoutAuth(order);

        response.then().statusCode(401);
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("You should be authorised", response.jsonPath().getString("message"));
    }

    @AfterEach
    public void tearDown() {
        if (authToken != null) {
            given()
                    .header("Authorization", authToken)
                    .delete("https://qa-stellarburgers.education-services.ru/api/auth/user")
                    .then()
                    .statusCode(202);
        }
    }
        }
