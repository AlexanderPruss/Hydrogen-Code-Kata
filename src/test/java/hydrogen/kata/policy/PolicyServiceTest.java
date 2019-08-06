package hydrogen.kata.policy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import hydrogen.kata.customer.Customer;
import hydrogen.kata.customer.CustomerRepository;
import hydrogen.kata.errors.EntityNotFoundException;
import hydrogen.kata.errors.ValidationException;
import hydrogen.kata.insurance.InsuranceModule;
import hydrogen.kata.insurance.InsuranceModuleRepository;
import hydrogen.kata.migrations.scripts.CreateInitialInsuranceModules;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DisplayName("PolicyService")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PolicyServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InsuranceModuleRepository insuranceModuleRepository;

    private final PolicyValidator validator = new PolicyValidator();

    @BeforeAll
    void addInsuranceModules() {
        CreateInitialInsuranceModules modulesCreator = new CreateInitialInsuranceModules(insuranceModuleRepository);
        modulesCreator.doMigration();
    }

    @Nested
    @DisplayName("#updatePolicy")
    class UpdatePolicy {

        @DisplayName("adds a policy to the customer")
        @Test
        void addAPolicy() throws ValidationException, EntityNotFoundException {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);

            Customer customer = createCustomer("Add a policy");
            List<InsuranceModule> insuranceModules = insuranceModuleRepository.findAll();
            InsuranceModule bikeModule = insuranceModules.stream().filter(module -> module.getName().contains("Sick Tricks")).findAny().get();
            PolicyDto newPolicy = PolicyDto.builder()
                    .coverage(1000)
                    .insuranceModuleId(bikeModule.getId())
                    .build();

            Customer updatedCustomer = service.updatePolicy(customer.getId(), newPolicy);

            Customer expectedCustomer = customer;
            expectedCustomer.getPolicies().add(Policy.builder().
                    coverage(1000)
                    .insuranceModule(bikeModule)
                    .price(300)
                    .build());
            assertThat(updatedCustomer).isEqualTo(expectedCustomer);
        }

        @DisplayName("updates a policy of a customer")
        @Test
        void updateAPolicy() throws ValidationException, EntityNotFoundException {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);

            Customer customer = createCustomer("Update a policy");
            List<InsuranceModule> insuranceModules = insuranceModuleRepository.findAll();
            InsuranceModule bikeModule = insuranceModules.stream().filter(module -> module.getName().contains("Sick Tricks")).findAny().get();
            PolicyDto newPolicy = PolicyDto.builder()
                    .coverage(1000)
                    .insuranceModuleId(bikeModule.getId())
                    .build();
            PolicyDto updatedPolicy = PolicyDto.builder()
                    .coverage(2000)
                    .insuranceModuleId(bikeModule.getId())
                    .build();

            service.updatePolicy(customer.getId(), newPolicy);
            Customer updatedCustomer = service.updatePolicy(customer.getId(), updatedPolicy);

            Customer expectedCustomer = customer;
            expectedCustomer.getPolicies().add(Policy.builder().
                    coverage(2000)
                    .insuranceModule(bikeModule)
                    .price(600)
                    .build());
            assertThat(updatedCustomer).isEqualTo(expectedCustomer);
        }

        @DisplayName("throws an error if the customer can't be found")
        @Test
        void missingCustomer() {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);

            assertThatThrownBy(() -> service.updatePolicy("fake customer", null))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @DisplayName("throws an error if the insurance module can't be found")
        @Test
        void missingInsuranceModule() {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);

            Customer customer = createCustomer("Bad insurance module");
            PolicyDto badPolicy = PolicyDto.builder()
                    .coverage(1000)
                    .insuranceModuleId("trolololo")
                    .build();

            assertThatThrownBy(() -> service.updatePolicy(customer.getId(), badPolicy))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @DisplayName("throws an error if there's a validation error")
        @Test
        void invalidInput() {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);
            List<InsuranceModule> insuranceModules = insuranceModuleRepository.findAll();

            Customer customer = createCustomer("Invalid input");
            PolicyDto badPolicy = PolicyDto.builder()
                    .coverage(-1)
                    .insuranceModuleId(insuranceModules.get(0).getId())
                    .build();

            assertThatThrownBy(() -> service.updatePolicy(customer.getId(), badPolicy))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Nested
    @DisplayName("#deletePolicy")
    class DeletePolicy {

        @DisplayName("deletes the customer's given policy")
        @Test
        void deleteAPolicy() throws ValidationException, EntityNotFoundException {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);

            Customer customer = createCustomer("Delete a policy");
            List<InsuranceModule> insuranceModules = insuranceModuleRepository.findAll();
            InsuranceModule bikeModule = insuranceModules.stream().filter(module -> module.getName().contains("Sick Tricks")).findAny().get();
            PolicyDto newPolicy = PolicyDto.builder()
                    .coverage(1000)
                    .insuranceModuleId(bikeModule.getId())
                    .build();
            service.updatePolicy(customer.getId(), newPolicy);

            Customer updatedCustomer = service.deletePolicy(customer.getId(), bikeModule.getId());

            assertThat(updatedCustomer).isEqualTo(customer);
        }

        @DisplayName("does not throw an error if no matching policy is found")
        @Test
        void missingPolicyToDelete() throws EntityNotFoundException {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);

            Customer customer = createCustomer("Delete a policy");
            Customer updatedCustomer = service.deletePolicy(customer.getId(), "random module");

            assertThat(updatedCustomer).isEqualTo(customer);
        }

        @DisplayName("throws an error if the customer can't be found")
        @Test
        void missingCustomer() {
            PolicyService service = new PolicyService(customerRepository, insuranceModuleRepository, validator);

            assertThatThrownBy(() -> service.deletePolicy("fake customer", "policy"))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    Customer createCustomer(String name) {
        return customerRepository.save(Customer.builder()
                .username(name)
                .policies(new ArrayList<>())
                .build());
    }

}
