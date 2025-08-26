package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public record Birthday(LocalDate value) {

    public Birthday {
        ValidationResult vr = new ValidationResult();

        if (vr.hasErrors()) {
            throw new DomainValidationException(String.join("; ", vr.getErrors()));
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