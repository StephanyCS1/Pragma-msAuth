package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;


public record EditUserCommand(
        String address,
        Email email,
        Salary baseSalary
) {

    public EditUserCommand {
        ValidationResult vr = new ValidationResult();
        validateCommand(address, email, baseSalary, vr);

        if (vr.hasErrors()) {
            throw new DomainValidationException(vr.getErrors());
        }
    }

    private static void validateCommand(String address, Email email, Salary baseSalary, ValidationResult vr) {
        validateAddress(address, vr);
        validateEmail(email, vr);
        validateSalary(baseSalary, vr);
    }

    private static void validateAddress(String address, ValidationResult vr) {
        if (address == null || address.trim().isEmpty()) {
            vr.addError("La dirección es obligatoria");
            return;
        }
        if (address.trim().length() < 10) {
            vr.addError("La dirección debe tener al menos 10 caracteres");
        }
        if (address.trim().length() > 200) {
            vr.addError("La dirección no puede tener más de 200 caracteres");
        }
    }

    private static void validateEmail(Email email, ValidationResult vr) {
        if (email == null) {
            vr.addError("El email es obligatorio");
        }
    }

    private static void validateSalary(Salary baseSalary, ValidationResult vr) {
        if (baseSalary == null) {
            vr.addError("El salario base es obligatorio");
        }
    }
}