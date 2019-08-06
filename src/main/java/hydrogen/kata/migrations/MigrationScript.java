package hydrogen.kata.migrations;

public interface MigrationScript {

    String getName();

    String getVersion();

    /**
     * Executes the migration. Some scripts can have a fail-state that's not an error-state; they communicate this
     * by returning a boolean indicating if the script was successful.
     */
    boolean doMigration();

}
