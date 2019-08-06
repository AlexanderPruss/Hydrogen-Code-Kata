package hydrogen.kata.validation;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationError {

    private final List<String> fields;
    private final String errorMessage;

    public ValidationError(List<String> fields, String errorMessage) {
        this.fields = fields;
        this.errorMessage = errorMessage;
    }

    public String getExtendedMessage() {
        return String.join(",", fields) + ":" + errorMessage;
    }
}
