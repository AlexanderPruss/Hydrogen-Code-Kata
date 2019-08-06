package hydrogen.kata.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import hydrogen.kata.insurance.InsuranceModule;
import hydrogen.kata.validation.ValidationErrors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PolicyValidator")
public class PolicyValidatorTest {

    private final PolicyValidator validator = new PolicyValidator();

    private final InsuranceModule insuranceModule = InsuranceModule.builder()
            .coverageMinimum(100)
            .coverageMaximum(1000)
            .build();

    @Nested
    @DisplayName("#validate")
    class UpdatePolicy {

        @DisplayName("returns null if no errors are found")
        @Test
        void addAPolicy() {
            PolicyDto validPolicy = PolicyDto.builder().coverage(200).build();

            ValidationErrors errors = validator.validate(validPolicy, insuranceModule);

            assertThat(errors).isNull();
        }

        @DisplayName("returns an error if the coverage is too high")
        @Test
        void coverageTooHigh() {
            PolicyDto validPolicy = PolicyDto.builder().coverage(1200).build();

            ValidationErrors errors = validator.validate(validPolicy, insuranceModule);

            assertThat(errors.getErrors()).hasSize(1);
            assertThat(errors.getErrors().get(0).getFields()).hasSize(2);
            assertThat(errors.getErrors().get(0).getFields().get(0)).isEqualTo("policy.coverage");
            assertThat(errors.getErrors().get(0).getFields().get(1)).isEqualTo("policy.insuranceModule.coverageMaximum");
        }

        @DisplayName("returns an error if the coverage is too low")
        @Test
        void coverageTooLow() {
            PolicyDto validPolicy = PolicyDto.builder().coverage(50).build();

            ValidationErrors errors = validator.validate(validPolicy, insuranceModule);

            assertThat(errors.getErrors()).hasSize(1);
            assertThat(errors.getErrors().get(0).getFields()).hasSize(2);
            assertThat(errors.getErrors().get(0).getFields().get(0)).isEqualTo("policy.coverage");
            assertThat(errors.getErrors().get(0).getFields().get(1)).isEqualTo("policy.insuranceModule.coverageMinimum");
        }

        /**
         * This is just being defensive.
         */
        @DisplayName("returns an error if the coverage is negative")
        @Test
        void coverageIsNegative() {
            PolicyDto validPolicy = PolicyDto.builder().coverage(-50).build();

            ValidationErrors errors = validator.validate(validPolicy, insuranceModule);

            //We get two errors - one for a negative coverage, one for a coverage that's lower than the coverage minimum.

            assertThat(errors.getErrors()).hasSize(2);
            assertThat(errors.getErrors().get(0).getFields()).hasSize(1);
            assertThat(errors.getErrors().get(0).getFields().get(0)).isEqualTo("policy.coverage");
            assertThat(errors.getErrors().get(1).getFields()).hasSize(2);
            assertThat(errors.getErrors().get(1).getFields().get(0)).isEqualTo("policy.coverage");
            assertThat(errors.getErrors().get(1).getFields().get(1)).isEqualTo("policy.insuranceModule.coverageMinimum");
        }

    }

}
