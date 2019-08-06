package hydrogen.kata.migrations;

import hydrogen.kata.migrations.scripts.CreateInitialInsuranceModules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration is in charge of choosing which migration, if any, should be executed.
 * <p>
 * This is a bit more elegant in node.
 */
@Configuration
public class MigrationConfiguration {

    private final CreateInitialInsuranceModules currentMigrationScript;

    public MigrationConfiguration(CreateInitialInsuranceModules currentMigrationScript) {
        this.currentMigrationScript = currentMigrationScript;
    }

    @Bean("currentMigrationScript")
    public MigrationScript getCurrentMigrationScript() {
        return currentMigrationScript;
    }


}
