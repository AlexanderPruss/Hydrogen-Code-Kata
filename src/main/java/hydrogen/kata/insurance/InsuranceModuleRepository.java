package hydrogen.kata.insurance;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface InsuranceModuleRepository extends MongoRepository<InsuranceModule, String> {
}
