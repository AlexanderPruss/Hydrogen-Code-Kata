package hydrogen.kata.policy;

import hydrogen.kata.customer.Customer;
import hydrogen.kata.response.ResponseWrapper;
import org.springframework.web.bind.annotation.*;

@RestController
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    /**
     * Adds the policy to the user's list of policies if the user has no policy for the given insurance module.
     * <p>
     * If the user already has a matching policy, that policy is overwritten.
     */
    @PutMapping("/customers/{customerId}/policies")
    public ResponseWrapper<Customer> updatePolicy(@PathVariable String customerId, @RequestBody PolicyDto policyDto) {
        return ResponseWrapper.of(policyService.updatePolicy(customerId, policyDto));
    }

    @DeleteMapping("/customers/{customerId}/policies")
    public ResponseWrapper<Customer> deletePolicy(@PathVariable String customerId, @RequestBody String insuranceModuleId) {
        return ResponseWrapper.of(policyService.deletePolicy(customerId, insuranceModuleId));
    }
}
