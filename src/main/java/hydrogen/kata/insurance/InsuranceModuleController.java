package hydrogen.kata.insurance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import hydrogen.kata.response.ResponseWrapper;

import java.util.Collection;

@RestController
public class InsuranceModuleController {

    private final InsuranceModuleRepository insuranceModuleRepository;

    public InsuranceModuleController(InsuranceModuleRepository insuranceModuleRepository) {
        this.insuranceModuleRepository = insuranceModuleRepository;
    }

    @GetMapping("/insurance-modules")
    public ResponseWrapper<Collection<InsuranceModule>> getInsuranceModules() {
        return ResponseWrapper.of(insuranceModuleRepository.findAll());
    }

}
