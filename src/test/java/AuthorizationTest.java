import Generate.UserGenerator;
import Models.User;
import Models.UserCreds;
import Users.UserActions;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorizationTest {

    private UserActions userActions;
    private User testUser;

    @BeforeEach
    public void setUp() {
        userActions = new UserActions();

        testUser = UserGenerator.randomUser();
        userActions.create(testUser).then().statusCode(200);

    }

    @Test
    @Description("Успешная авторизация пользователя")
    public void authorization() {

        UserCreds userCreds = new UserCreds()
                .setEmail(testUser.getEmail())
                .setPassword(testUser.getPassword());

        Response login = userActions.login(userCreds);
        login.then().statusCode(200);
        assertTrue(login.jsonPath().getBoolean("success"));
        assertNotNull(login.jsonPath().getString("accessToken"));
    }

    @Test
    @Description("Авторизация с неверным email")
    public void loginWithNonExistentUser() {
        UserCreds userCreds = new UserCreds()
                .setEmail("mail@mail.ru" + System.currentTimeMillis())
                .setPassword("Qaz123");

        userActions.login(userCreds)
                .then()
                .statusCode(401)
                .body("message", is("email or password are incorrect"));

    }

    @AfterEach
    public void tearDown() {
        if (testUser.getAccessToken() != null) {
            userActions.deleteUser(testUser.getAccessToken())
                    .then()
                    .statusCode(200);
        }

    }
}


