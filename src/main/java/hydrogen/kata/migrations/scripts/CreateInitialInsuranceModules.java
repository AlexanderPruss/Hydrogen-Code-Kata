package hydrogen.kata.migrations.scripts;

import hydrogen.kata.insurance.InsuranceModule;
import hydrogen.kata.insurance.InsuranceModuleRepository;
import hydrogen.kata.migrations.MigrationScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates the initial four insurance modules for the challenge (bike, jewelry, electronics, sports equipment)
 */
@Component
public class CreateInitialInsuranceModules implements MigrationScript {

    private final InsuranceModuleRepository insuranceRepository;

    public CreateInitialInsuranceModules(InsuranceModuleRepository insuranceRepository) {
        this.insuranceRepository = insuranceRepository;
    }

    @Override
    public String getName() {
        return CreateInitialInsuranceModules.class.getSimpleName();
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean doMigration() {
        Collection<InsuranceModule> initialModules = new ArrayList<>();
        initialModules.add(InsuranceModule.builder()
                .name("Sick Tricks Bike and Trike Insurance")
                .coverageMinimum(0)
                .coverageMaximum(3000 * 100)
                .riskPercentage(30)
                .imageUrl("https://prusselements.s3.eu-central-1.amazonaws.com/bike.jpg")
                .build());
        initialModules.add(InsuranceModule.builder()
                .name("Fanciest Jewels Wow Ever Such Insurance")
                .coverageMinimum(500 * 100)
                .coverageMaximum(10000 * 100)
                .riskPercentage(5)
                .imageUrl("https://prusselements.s3.eu-central-1.amazonaws.com/catcatcat.jpg")
                .build());
        initialModules.add(InsuranceModule.builder()
                .name("Never Fear Electro Man Is Here (to provide insurance)")
                .coverageMinimum(500 * 100)
                .coverageMaximum(6000 * 100)
                .riskPercentage(35)
                .imageUrl("https://prusselements.s3.eu-central-1.amazonaws.com/electroman.png")
                .build());
        initialModules.add(InsuranceModule.builder()
                .name("Unbelievable Play Insurance")
                .coverageMinimum(0)
                .coverageMaximum(20000 * 100)
                .riskPercentage(30)
                .imageUrl("https://prusselements.s3.eu-central-1.amazonaws.com/unbelievablesports.jpg")
                .build());
        insuranceRepository.saveAll(initialModules);
        return true;
    }
}
