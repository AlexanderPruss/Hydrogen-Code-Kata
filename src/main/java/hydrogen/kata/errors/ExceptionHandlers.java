package hydrogen.kata.errors;

import hydrogen.kata.response.ErrorResponse;
import hydrogen.kata.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ExceptionHandlers {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseWrapper handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.error("Entity Not Found", exception);
        return ResponseWrapper.ofError(new ErrorResponse("ENTITY_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseWrapper handleValidationException(final ValidationException exception) {
        log.error("validation error", exception);
        return ResponseWrapper.ofError(new ErrorResponse("VALIDATION_ERROR", exception.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseWrapper handleThrowable(final Throwable exception) {
        log.error("Internal Error", exception);
        return ResponseWrapper.ofError(new ErrorResponse("INTERNAL_SERVER_ERROR", exception.getMessage()));
    }
}
