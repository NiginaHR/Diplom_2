package models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
    @Accessors(chain = true)
    public class User {
        private String email;
        private String password;
        private String name;
        private String accessToken;
        private String refreshToken;
        private List<String> ingredients;
    }
