package hydrogen.kata.migrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This is a pattern I like to use when working with MongoDB. It's inspired by migration libraries like Flyway, but is
 * much more lightweight.
 * <p>
 * If there is a currently active migration, and it the migration has not yet been executed, the migration service
 * attempts to execute it on startup.
 * <p>
 * I've adapted this from my implementation in Node.js.
 */
@Service
@Slf4j
public class MigrationService {

    private final MigrationRepository migrationRepository;

    public MigrationService(MigrationRepository migrationRepository, MigrationScript currentMigrationScript) {
        this.migrationRepository = migrationRepository;

        executeMigration(currentMigrationScript);
    }

    private void executeMigration(MigrationScript script) {
        if (script == null) {
            log.info("There is no current database migration.");
            return;
        }

        DatabaseMigration existingMigration = migrationRepository.findByName(script.getName());
        if (existingMigration != null && existingMigration.isSuccessful()) {
            log.info("Migration {} has already been executed", script.getName());
            return;
        }

        log.info("Executing migration {}", script.getName());
        boolean success = script.doMigration();
        if (!success) {
            log.error("Migration {} was not executed successfully", script.getName());
        }

        DatabaseMigration newMigration = DatabaseMigration.builder()
                .name(script.getName())
                .successful(success)
                .version(script.getVersion())
                .build();
        migrationRepository.save(newMigration);
    }


}
