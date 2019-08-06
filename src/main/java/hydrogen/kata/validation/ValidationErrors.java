package hydrogen.kata.validation;

import hydrogen.kata.errors.ValidationException;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ValidationErrors {

    private final List<ValidationError> errors;

    public ValidationErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public ValidationException toException() {
        Collection<String> errorMessages = errors.stream()
                .map(ValidationError::getExtendedMessage).collect(Collectors.toList());
        String errorMessage = String.join("\n", errorMessages);
        return new ValidationException(errorMessage);
    }
}
