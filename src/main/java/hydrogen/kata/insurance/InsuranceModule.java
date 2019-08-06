package hydrogen.kata.insurance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * Represents a class of insurances, such as Bicycle insurance, Life insurance, etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceModule {

    @Id
    private String id;

    /**
     * Coverage, like all money values, is saved in cents.
     */
    private int coverageMinimum;

    /**
     * Coverage, like all money values, is saved in cents.
     */
    private int coverageMaximum;

    /**
     * Keeping it simple for the challenge - risk is an int from 0-100.
     */
    private int riskPercentage;

    private String name;
    private String imageUrl;
}
