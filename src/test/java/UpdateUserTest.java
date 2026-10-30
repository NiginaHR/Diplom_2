import generate.UserGenerator;
import models.User;
import users.UserActions;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateUserTest {

    private UserActions userActions;
    private User testUser;

    @BeforeEach
    public void setUp() {
        userActions = new UserActions();

        testUser = UserGenerator.randomUser();
        Response createResponse = userActions.create(testUser);

        createResponse.then().statusCode(200);

        String token = createResponse.jsonPath().getString("accessToken");
        testUser.setAccessToken(token);
    }


    @Test
    @Description("Успешное обновление имени пользователя")
    public void updateName() {

        testUser.setName("User1" + System.currentTimeMillis());

        Response updateResponse = userActions.update(testUser);

        updateResponse.then().statusCode(200);
        assertTrue(updateResponse.jsonPath().getBoolean("success"));
        assertEquals(testUser.getName(), updateResponse.jsonPath().getString("user.name"));
    }

    @Test
    @Description("Успешное обновление почты пользователя")
    public void updateEmail() {

        testUser.setEmail("test@mail.ru" + System.currentTimeMillis());

        Response updateResponse = userActions.update(testUser);

        updateResponse.then().statusCode(200);
        assertTrue(updateResponse.jsonPath().getBoolean("success"));
        assertEquals(testUser.getEmail(), updateResponse.jsonPath().getString("user.email"));
    }

    @Test
    @Description("Успешное обновление пароля пользователя")
    public void updatePassword() {

        testUser.setPassword("23546S" + System.currentTimeMillis());

        Response updateResponse = userActions.update(testUser);

        updateResponse.then().statusCode(200);
        assertTrue(updateResponse.jsonPath().getBoolean("success"));
    }


    @Test
    @Description("Обновление имени пользователя без авторизации")
    public void updateNameWithoutAuth() {
        testUser.setName("User1" + System.currentTimeMillis());

        userActions.updateWithoutAuth(testUser)
                .then()
                .statusCode(401)
                .body("message", is("You should be authorised"));


    }

    @Test
    @Description("Обновление почты пользователя без авторизации")
    public void updateEmailWithoutAuth() {
        testUser.setEmail("test@mail.ru" + System.currentTimeMillis());

        userActions.updateWithoutAuth(testUser)
                .then()
                .statusCode(401)
                .body("message", is("You should be authorised"));


    }

    @Test
    @Description("Обновление пароля пользователя без авторизации")
    public void updatePasswordWithoutAuth() {
        testUser.setPassword("23546S" + System.currentTimeMillis());

        userActions.updateWithoutAuth(testUser)
                .then()
                .statusCode(401)
                .body("message", is("You should be authorised"));


    }

    @AfterEach
    public void tearDown() {
        if (testUser.getAccessToken() != null) {
            userActions.deleteUser(testUser.getAccessToken())
                    .then()
                    .statusCode(202);
        }
    }
}

