package realtimemariadb;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class Config {

    @Singleton
    DatabaseListener databaseListener(MainChannel mainChannel) {
        return new DatabaseListener(mainChannel);
    }

}
