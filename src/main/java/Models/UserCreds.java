package Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
    @Data
    @Accessors(chain = true)
    public class UserCreds {
        private String name;
        private String password;
        private String email;

    }




