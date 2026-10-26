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

public class CreateUserTest {

    private UserActions userActions;
    private User testUser;

    @BeforeEach
    public void setUp() {
        userActions = new UserActions();
        testUser = UserGenerator.randomUser();

        Response createResponse = userActions.create(testUser);
        createResponse.then().statusCode(200);


        String token = createResponse.jsonPath().getString("accessToken");
        String name = createResponse.jsonPath().getString("user.name");
        String email = createResponse.jsonPath().getString("user.email");

        testUser.setAccessToken(token);
        testUser.setName(name);
        testUser.setEmail(email);
    }

    @Test
    @Description("Успешная регистрация пользователя")
    public void registrationUser() {
        User newUser = UserGenerator.randomUser();
        userActions.create(newUser).then().statusCode(200);

        Response loginResp = userActions.login(new UserCreds()
                .setName(newUser.getName())
                .setPassword(newUser.getPassword())
                .setEmail(newUser.getEmail()));

    }

    @Test
    @Description("Повторная регистрация существующего пользователя")
    public void repeatedRegistrationUser() {
        Response response = userActions.create(testUser);

        response.then()
                .statusCode(403)
                .body("message", is("User already exists"));
    }

    @Test
    @Description("Регистрация пользователя без заполнения обязательного поля")
    public void registrationUserWithoutEmail() {
        User badUser = new User()
                .setName("Чарли")
                .setPassword("Qaz123");

        userActions.create(badUser).then()

                .statusCode(403)
                .body("message", is("Email, password and name are required fields"));


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
