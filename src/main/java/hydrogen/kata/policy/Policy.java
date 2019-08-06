package hydrogen.kata.policy;

import hydrogen.kata.insurance.InsuranceModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import hydrogen.kata.customer.Customer;

/**
 * A policy is a contract between a {@link Customer} and an {@link InsuranceModule}.
 * <p>
 * The policy also saves a copy of the InsuranceModule at the time of signing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    private int coverage;
    private int price;

    private InsuranceModule insuranceModule;
}
