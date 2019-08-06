package hydrogen.kata.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import hydrogen.kata.response.ResponseWrapper;

import java.util.Collections;

/**
 * This is the most improvised bit of the whole challenge. Doing proper user handling isn't difficult,
 * but takes a nontrivial amount of time; instead, I'm just doing a handwavy approach that's obviously not production ready.
 */
@RestController
@Slf4j
public class LoginController {

    private final CustomerRepository customerRepository;

    public LoginController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Logs a user in, no questions asked. If the user doesn't exist, they're created.
     */
    @PostMapping("/login")
    public ResponseWrapper<Customer> login(@RequestBody String username) {
        log.info("Logging in user {}", username);

        Customer existingCustomer = customerRepository.findByUsername(username);
        if (existingCustomer != null) {
            log.debug("User {} already exists", username);
            return ResponseWrapper.of(existingCustomer);
        }

        log.info("Creating a new user for username {}", username);
        Customer customer = Customer.builder()
                .username(username)
                .policies(Collections.emptyList())
                .build();
        return ResponseWrapper.of(customerRepository.save(customer));
    }
}
