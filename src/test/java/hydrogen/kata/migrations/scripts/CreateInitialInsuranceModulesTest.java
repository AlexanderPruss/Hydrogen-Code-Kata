package hydrogen.kata.migrations.scripts;

import hydrogen.kata.insurance.InsuranceModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import hydrogen.kata.insurance.InsuranceModuleRepository;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DisplayName("CreateInitialInsuranceModules")
public class CreateInitialInsuranceModulesTest {

    @Autowired
    private InsuranceModuleRepository insuranceRepository;

    @Nested
    @DisplayName("#doMigration")
    class DoMigration {

        @DisplayName("it saves bicycle, electric, jewelry, and sports insurance into the DB")
        @Test
        void createInitialInsuranceModules() {
            CreateInitialInsuranceModules migration = new CreateInitialInsuranceModules(insuranceRepository);
            migration.doMigration();
            Collection<InsuranceModule> insuranceModules = insuranceRepository.findAll();

            //I don't think it's important to test that each field was written correctly, that's just copying the implementation.
            //Instead, we'll just test that each of the modules exists.
            Optional<InsuranceModule> bicycleOptional = insuranceModules.stream()
                    .filter((InsuranceModule module) -> module.getName().equals("Sick Tricks Bike and Trike Insurance"))
                    .findFirst();
            Optional<InsuranceModule> electricityOptional = insuranceModules.stream()
                    .filter((InsuranceModule module) -> module.getName().equals("Never Fear Electro Man Is Here (to provide insurance)"))
                    .findFirst();
            Optional<InsuranceModule> jewelryOptional = insuranceModules.stream()
                    .filter((InsuranceModule module) -> module.getName().equals("Fanciest Jewels Wow Ever Such Insurance"))
                    .findFirst();
            Optional<InsuranceModule> sportsOptional = insuranceModules.stream()
                    .filter((InsuranceModule module) -> module.getName().equals("Unbelievable Play Insurance"))
                    .findFirst();

            assertThat(insuranceModules).hasSize(4);
            assertThat(bicycleOptional.isPresent()).isTrue();
            assertThat(electricityOptional.isPresent()).isTrue();
            assertThat(jewelryOptional.isPresent()).isTrue();
            assertThat(sportsOptional.isPresent()).isTrue();
        }
    }

}
