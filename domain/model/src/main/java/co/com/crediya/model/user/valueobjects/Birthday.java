package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public record Birthday(LocalDate value) {

    public Birthday {
        ValidationResult vr = new ValidationResult();
        validate(value, vr);

        if (vr.hasErrors()) {
            throw new DomainValidationException(String.join("; ", vr.getErrors()));
        }
    }

    public static void validate(LocalDate value, ValidationResult vr) {
        if (value == null) {
            vr.addError("La fecha de nacimiento es obligatoria");
            return;
        }

        LocalDate today = LocalDate.now();

        // Validar que no sea fecha futura
        if (value.isAfter(today)) {
            vr.addError("La fecha de nacimiento no puede ser futura");
        }

        // Validar que no tenga más de 120 años
        LocalDate maxValidDate = today.minusYears(120);
        if (value.isBefore(maxValidDate)) {
            vr.addError("La fecha de nacimiento no puede ser anterior a " + maxValidDate);
        }
    }

    public static Birthday of(String isoDate) {
        try {
            return new Birthday(LocalDate.parse(isoDate));
        } catch (DateTimeParseException e) {
            throw new DomainValidationException("Formato de fecha inválido, use yyyy-MM-dd");
        }
    }
}