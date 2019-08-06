package hydrogen.kata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import hydrogen.kata.customer.Customer;
import hydrogen.kata.insurance.InsuranceModule;
import hydrogen.kata.policy.Policy;
import hydrogen.kata.policy.PolicyDto;
import hydrogen.kata.response.ResponseWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * I"m keeping the integration tests minimal for now. It's reasonable to split these into multiple test classes
 * once the app becomes more complicated.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DisplayName("Integration Tests")
public class HydrogenKataApplicationIntegrationTests {

    @LocalServerPort
    int port;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("The application starts")
    @Test
    public void contextLoads() {
    }

    @DisplayName("A new user can be created")
    @Test
    public void newUserCanBeCreated() {
        ResponseEntity<ResponseWrapper> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/login", "newUserCanBeCreated", ResponseWrapper.class);
        Customer customer = objectMapper.convertValue(loginResponse.getBody().getBody(), Customer.class);

        Customer expectedCustomer = Customer.builder()
                .policies(Collections.emptyList())
                .username("newUserCanBeCreated")
                .id(customer.getId())
                .build();
        assertThat(customer).isEqualTo(expectedCustomer);
    }

    /**
     * Here we'll create a user, add two policies, and then change one of the policies.
     */
    @DisplayName("User policies can be added and changed.")
    @Test
    public void userPoliciesGetAddedAndChanged() {
        ResponseEntity<ResponseWrapper> insuranceModulesResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/insurance-modules", ResponseWrapper.class);
        List insuranceDumbTypes = objectMapper.convertValue(insuranceModulesResponse.getBody().getBody(), List.class);
        List<InsuranceModule> insuranceModules = new ArrayList<>();
        insuranceDumbTypes.forEach(object -> insuranceModules.add(
                objectMapper.convertValue(object, InsuranceModule.class)
        ));
        InsuranceModule bikeModule = insuranceModules.stream().filter(module -> module.getName().contains("Sick Tricks")).findAny().get();
        InsuranceModule jewelryModule = insuranceModules.stream().filter(module -> module.getName().contains("Fanciest Jewels")).findAny().get();

        ResponseEntity<ResponseWrapper> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/login", "userWithPolicies", ResponseWrapper.class);
        Customer customer = objectMapper.convertValue(loginResponse.getBody().getBody(), Customer.class);
        String customerId = customer.getId();

        PolicyDto firstPolicy = PolicyDto.builder()
                .coverage(bikeModule.getCoverageMinimum())
                .insuranceModuleId(bikeModule.getId())
                .build();
        PolicyDto secondPolicy = PolicyDto.builder()
                .coverage(jewelryModule.getCoverageMinimum())
                .insuranceModuleId(jewelryModule.getId())
                .build();
        PolicyDto secondPolicyChanged = PolicyDto.builder()
                .coverage(jewelryModule.getCoverageMinimum() + 1000)
                .insuranceModuleId(jewelryModule.getId())
                .build();
        HttpEntity<PolicyDto> firstPolicyEntity = new HttpEntity<>(firstPolicy);
        HttpEntity<PolicyDto> secondPolicyEntity = new HttpEntity<>(secondPolicy);
        HttpEntity<PolicyDto> changedPolicyEntity = new HttpEntity<>(secondPolicyChanged);
        restTemplate.exchange("http://localhost:" + port + "/customers/" + customerId + "/policies", HttpMethod.PUT, firstPolicyEntity, ResponseWrapper.class);
        restTemplate.exchange("http://localhost:" + port + "/customers/" + customerId + "/policies", HttpMethod.PUT, secondPolicyEntity, ResponseWrapper.class);
        ResponseEntity<ResponseWrapper> resultingCustomerResponse = restTemplate.exchange("http://localhost:" + port + "/customers/" + customerId + "/policies", HttpMethod.PUT, changedPolicyEntity, ResponseWrapper.class);

        Customer resultingCustomer = objectMapper.convertValue(resultingCustomerResponse.getBody().getBody(), Customer.class);
        Policy expectedBikePolicy = Policy.builder()
                .coverage(bikeModule.getCoverageMinimum())
                .insuranceModule(bikeModule)
                .price(0)
                .build();
        Policy expectedJewelryPolicy = Policy.builder()
                .coverage(51000)
                .insuranceModule(jewelryModule)
                .price(2550)
                .build();

        assertThat(resultingCustomer.getId()).isEqualTo(customerId);
        assertThat(resultingCustomer.getPolicies()).hasSize(2);
        assertThat(resultingCustomer.getPolicies()).containsExactlyInAnyOrder(expectedBikePolicy, expectedJewelryPolicy);
    }

    /**
     * Here we'll create a user, save two policies, and erase one.
     */
    @DisplayName("User policies can be deleted.")
    @Test
    public void userPoliciesGetDeleted() {
        ResponseEntity<ResponseWrapper> insuranceModulesResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/insurance-modules", ResponseWrapper.class);
        List insuranceDumbTypes = objectMapper.convertValue(insuranceModulesResponse.getBody().getBody(), List.class);
        List<InsuranceModule> insuranceModules = new ArrayList<>();
        insuranceDumbTypes.forEach(object -> insuranceModules.add(
                objectMapper.convertValue(object, InsuranceModule.class)
        ));
        InsuranceModule bikeModule = insuranceModules.stream().filter(module -> module.getName().contains("Sick Tricks")).findAny().get();
        InsuranceModule jewelryModule = insuranceModules.stream().filter(module -> module.getName().contains("Fanciest Jewels")).findAny().get();

        ResponseEntity<ResponseWrapper> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/login", "userWithDeletedPolicies", ResponseWrapper.class);
        Customer customer = objectMapper.convertValue(loginResponse.getBody().getBody(), Customer.class);
        String customerId = customer.getId();

        PolicyDto firstPolicy = PolicyDto.builder()
                .coverage(bikeModule.getCoverageMinimum())
                .insuranceModuleId(bikeModule.getId())
                .build();
        PolicyDto secondPolicy = PolicyDto.builder()
                .coverage(jewelryModule.getCoverageMinimum())
                .insuranceModuleId(jewelryModule.getId())
                .build();
        HttpEntity<PolicyDto> firstPolicyEntity = new HttpEntity<>(firstPolicy);
        HttpEntity<PolicyDto> secondPolicyEntity = new HttpEntity<>(secondPolicy);
        HttpEntity<String> deleteEntity = new HttpEntity<>(jewelryModule.getId());
        restTemplate.exchange("http://localhost:" + port + "/customers/" + customerId + "/policies", HttpMethod.PUT, firstPolicyEntity, ResponseWrapper.class);
        restTemplate.exchange("http://localhost:" + port + "/customers/" + customerId + "/policies", HttpMethod.PUT, secondPolicyEntity, ResponseWrapper.class);
        ResponseEntity<ResponseWrapper> resultingCustomerResponse = restTemplate.exchange("http://localhost:" + port + "/customers/" + customerId + "/policies", HttpMethod.DELETE, deleteEntity, ResponseWrapper.class);

        Customer resultingCustomer = objectMapper.convertValue(resultingCustomerResponse.getBody().getBody(), Customer.class);
        assertThat(resultingCustomer.getId()).isEqualTo(customerId);
        assertThat(resultingCustomer.getPolicies()).hasSize(1);
        assertThat(resultingCustomer.getPolicies().get(0).getInsuranceModule().getId()).isEqualTo(bikeModule.getId());
    }

}
