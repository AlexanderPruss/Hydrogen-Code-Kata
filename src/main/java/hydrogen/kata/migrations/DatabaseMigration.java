package hydrogen.kata.migrations;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class DatabaseMigration {

    @Id
    private String id;

    private String version;
    private String name;
    private boolean successful;
}
