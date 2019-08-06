package hydrogen.kata.response;

import lombok.Data;

@Data
public class ResponseWrapper<T> {

    private T body;
    private ErrorResponse error;

    public ResponseWrapper() {
    }

    public ResponseWrapper(T body, ErrorResponse error) {
        this.body = body;
        this.error = error;
    }

    public static <U> ResponseWrapper<U> of(U body) {
        return new ResponseWrapper<>(body, null);
    }

    public static ResponseWrapper ofError(ErrorResponse errorResponse) {
        return new ResponseWrapper<>(null, errorResponse);
    }
}
