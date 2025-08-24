package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern emailRegex = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+\\.[a-z]{2,})$");

    public Email {
        ValidationResult vr = new  ValidationResult();
        validate(value, vr);

        if (vr.hasErrors()) {
            throw new DomainValidationException(String.join("; ", vr.getErrors()));
        }
    }

    public static void validate(String email, ValidationResult vr) {
        if (email == null) { vr.addError("El email es obligatorio"); return;
        }
        if (!emailRegex.matcher(email).matches()) {
            vr.addError("El email no tiene el formato correcto");
        }
    }

}