package hydrogen.kata.migrations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DisplayName("MigrationService")
public class MigrationServiceTest {

    @Autowired
    private MigrationRepository migrationRepository;

    @Nested
    @DisplayName("On Startup")
    class OnStartup {

        @DisplayName("it does nothing if there is no current migration")
        @Test
        void doesNothingIfNoMigration() {
            MigrationService migrationService = new MigrationService(migrationRepository, null);

            //Implicit Expectation - no error is thrown.
        }

        @DisplayName("it does nothing if the migration is already successful")
        @Test
        void doesNothingIfMigrationSuccessful() {
            String name = "this already succeeded";
            DatabaseMigration successfulMigration = DatabaseMigration.builder()
                    .successful(true)
                    .version("abc")
                    .name(name)
                    .build();
            migrationRepository.save(successfulMigration);

            //If this migration is executed, the test will throw an error.
            MigrationScript shouldntRun = new MigrationScript() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getVersion() {
                    return "whatever";
                }

                @Override
                public boolean doMigration() {
                    throw new RuntimeException("BOOM");
                }
            };

            MigrationService migrationService = new MigrationService(migrationRepository, shouldntRun);

            //Implicit Expectation - no error is thrown.
        }

        @DisplayName("it executes and updates the status if the migration succeeds")
        @Test
        void savesSuccessfulMigrations() {
            String name = "this will succeed";
            DatabaseMigration expectedMigration = DatabaseMigration.builder()
                    .successful(true)
                    .version("abc")
                    .name(name)
                    .build();

            //If this migration is executed, the test will throw an error.
            MigrationScript script = new MigrationScript() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getVersion() {
                    return "abc";
                }

                @Override
                public boolean doMigration() {
                    return true;
                }
            };

            MigrationService migrationService = new MigrationService(migrationRepository, script);

            DatabaseMigration resultingMigration = migrationRepository.findByName(name);
            expectedMigration.setId(resultingMigration.getId());

            assertThat(resultingMigration).isEqualTo(expectedMigration);
        }

        @DisplayName("it executes and updates the status if the migration fails")
        @Test
        void savesFailedMigrations() {
            String name = "this will fail";
            DatabaseMigration expectedMigration = DatabaseMigration.builder()
                    .successful(false)
                    .version("abc")
                    .name(name)
                    .build();

            //If this migration is executed, the test will throw an error.
            MigrationScript script = new MigrationScript() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getVersion() {
                    return "abc";
                }

                @Override
                public boolean doMigration() {
                    return false;
                }
            };

            MigrationService migrationService = new MigrationService(migrationRepository, script);

            DatabaseMigration resultingMigration = migrationRepository.findByName(name);
            expectedMigration.setId(resultingMigration.getId());

            assertThat(resultingMigration).isEqualTo(expectedMigration);
        }

    }

}
