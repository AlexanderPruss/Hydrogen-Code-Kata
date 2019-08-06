package hydrogen.kata.migrations;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MigrationRepository extends MongoRepository<DatabaseMigration, String> {

    DatabaseMigration findByName(String name);
}
