package co.com.crediya.model.user.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public void addError(String message) { errors.add(message); }
    public void addAll(Collection<String> messages) { errors.addAll(messages); }
    public boolean hasErrors() { return !errors.isEmpty(); }
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }
}