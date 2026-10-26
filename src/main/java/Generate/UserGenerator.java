package Generate;

import Models.User;
import net.datafaker.Faker;

public class UserGenerator {
    private static final Faker faker = new Faker();

    public static User randomUser() {
        return new User()
                .setName(faker.name().name())
                .setPassword(faker.internet().password())
                .setEmail(faker.internet().emailAddress());
    }
}

