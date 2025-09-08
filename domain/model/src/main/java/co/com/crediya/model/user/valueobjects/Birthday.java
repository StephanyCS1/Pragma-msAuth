package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public record Birthday(LocalDate value) {

    public Birthday {
        validate(value);
    }

    private static void validate(LocalDate value) {
        if (value == null) {
            throw new DomainValidationException("La fecha de nacimiento es obligatoria");
        }

        LocalDate today = LocalDate.now();

        if (value.isAfter(today)) {
            throw new DomainValidationException("La fecha de nacimiento no puede ser futura");
        }

        LocalDate maxValidDate = today.minusYears(70);
        if (value.isBefore(maxValidDate)) {
            throw new DomainValidationException("La fecha de nacimiento no puede ser anterior a " + maxValidDate);
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