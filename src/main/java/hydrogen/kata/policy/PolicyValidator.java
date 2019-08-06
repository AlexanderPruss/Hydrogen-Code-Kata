package hydrogen.kata.policy;

import hydrogen.kata.insurance.InsuranceModule;
import org.springframework.stereotype.Component;
import hydrogen.kata.validation.ValidationError;
import hydrogen.kata.validation.ValidationErrors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class PolicyValidator {

    /**
     * Checks to see if the policy is valid. Returns errors if any are found, and null if the policy is fine.
     * <p>
     * I prefer doing this directly rather than using Spring magic for it.
     */
    public ValidationErrors validate(PolicyDto policyDto, InsuranceModule insuranceModule) {
        List<ValidationError> errors = new ArrayList<>();

        if (policyDto.getCoverage() < 0) {
            errors.add(new ValidationError(Collections.singletonList("policy.coverage"), "Coverage cannot be negative."));
        }
        if (policyDto.getCoverage() < insuranceModule.getCoverageMinimum()) {
            errors.add(new ValidationError(Arrays.asList("policy.coverage", "policy.insuranceModule.coverageMinimum"),
                    "Coverage cannot be smaller than the insurance module's minimum coverage."));
        }
        if (policyDto.getCoverage() > insuranceModule.getCoverageMaximum()) {
            errors.add(new ValidationError(Arrays.asList("policy.coverage", "policy.insuranceModule.coverageMaximum"),
                    "Coverage cannot be larger than the insurance module's maximum coverage."));
        }

        return errors.isEmpty() ? null : new ValidationErrors(errors);
    }
}
