package Users;

import Models.User;
import Models.UserCreds;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class UserActions {
    private static final String BASE_URL = "https://qa-stellarburgers.education-services.ru";

    @Step("Создание пользователя")
    public Response create(User user) {
        return given()
                .contentType(JSON)
                .body(user)
                .when()
                .post(BASE_URL + "/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response login(UserCreds creds) {
        return given()
                .contentType(JSON)
                .body(creds)
                .when()
                .post(BASE_URL + "/api/auth/login");
    }


    @Step("Изменение данных пользователя")
    public Response update(User user) {
        return given()
                .header("Authorization", user.getAccessToken())
                .contentType(JSON)
                .body(user)
                .when()
                .patch(BASE_URL + "/api/auth/user/");
    }

    @Step("Изменение данных пользователя без авторизации")
    public Response updateWithoutAuth(User user) {
        return given()
                .contentType(JSON)
                .body(user)
                .when()
                .patch(BASE_URL + "/api/auth/user/");


    }
    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .header("Authorization", token)
                .when()
                .delete(BASE_URL + "/api/auth/user");
    }

}

