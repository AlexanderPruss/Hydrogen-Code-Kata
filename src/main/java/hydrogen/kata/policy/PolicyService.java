package hydrogen.kata.policy;

import hydrogen.kata.customer.Customer;
import hydrogen.kata.customer.CustomerRepository;
import hydrogen.kata.insurance.InsuranceModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import hydrogen.kata.errors.EntityNotFoundException;
import hydrogen.kata.errors.ValidationException;
import hydrogen.kata.insurance.InsuranceModuleRepository;
import hydrogen.kata.validation.ValidationErrors;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Service
@Slf4j
public class PolicyService {

    private final CustomerRepository customerRepository;
    private final InsuranceModuleRepository insuranceRepository;
    private final PolicyValidator policyValidator;

    public PolicyService(CustomerRepository customerRepository, InsuranceModuleRepository insuranceRepository, PolicyValidator policyValidator) {
        this.customerRepository = customerRepository;
        this.insuranceRepository = insuranceRepository;
        this.policyValidator = policyValidator;
    }

    /**
     * Adds the policy to the user's list of policies if the user has no policy for the given insurance module.
     * <p>
     * If the user already has a matching policy, that policy is overwritten.
     */
    public Customer updatePolicy(String customerId, PolicyDto policyDto) {
        log.info("Updating a policy for user {}", customerId);

        Customer customer = findCustomer(customerId);

        Optional<InsuranceModule> insuranceModuleOptional = insuranceRepository.findById(policyDto.getInsuranceModuleId());
        if (!insuranceModuleOptional.isPresent()) {
            String message = "Couldn't find an insurance module with ID " + policyDto.getInsuranceModuleId();
            log.error(message);
            throw new EntityNotFoundException(message);
        }

        InsuranceModule insuranceModule = insuranceModuleOptional.get();
        ValidationErrors errors = policyValidator.validate(policyDto, insuranceModule);
        if (errors != null) {
            ValidationException exception = errors.toException();
            log.error(exception.getMessage());
            throw exception;
        }

        Policy policy = createPolicy(policyDto.getCoverage(), insuranceModule);
        return addOrReplacePolicy(customer, policy);
    }

    public Customer deletePolicy(String customerId, String insuranceModuleId) {
        log.info("Deleting a policy for user {}", customerId);
        Customer customer = findCustomer(customerId);
        Optional<Policy> policyOptional = customer.getPolicies().stream()
                .filter(policy -> policy.getInsuranceModule().getId().equals(insuranceModuleId))
                .findFirst();

        if (!policyOptional.isPresent()) {
            log.warn("Tried to remove a policy from user {}, but no matching policy was found.", customerId);
            return customer;
        }

        customer.getPolicies().remove(policyOptional.get());
        return customerRepository.save(customer);
    }

    private Customer findCustomer(String customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            String message = "Couldn't find a customer with ID " + customerId;
            log.error(message);
            throw new EntityNotFoundException(message);
        }
        return customerOptional.get();
    }

    private Customer addOrReplacePolicy(Customer customer, Policy policy) {
        OptionalInt policyIndexOptional = IntStream.range(0, customer.getPolicies().size())
                .filter(index -> customer.getPolicies().get(index).getInsuranceModule().getId()
                        .equals(policy.getInsuranceModule().getId()))
                .findFirst();

        if (!policyIndexOptional.isPresent()) {
            log.info("Creating a new policy for user {}", customer.getId());
            customer.getPolicies().add(policy);
            return customerRepository.save(customer);
        }

        log.info("Updating an existing policy for user {}", customer.getId());
        customer.getPolicies().set(policyIndexOptional.getAsInt(), policy);
        return customerRepository.save(customer);
    }

    private Policy createPolicy(int coverage, InsuranceModule insuranceModule) {
        int price = (int) Math.round(insuranceModule.getRiskPercentage() * coverage / 100.0);
        return Policy.builder()
                .coverage(coverage)
                .price(price)
                .insuranceModule(insuranceModule)
                .build();
    }
}
