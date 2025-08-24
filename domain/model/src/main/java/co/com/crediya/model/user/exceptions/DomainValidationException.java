package co.com.crediya.model.user.exceptions;

import java.util.List;

public class DomainValidationException extends RuntimeException {
    private final List<String> errors;

    public DomainValidationException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public DomainValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}