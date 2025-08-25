package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;

public record CreateUserCommand(
        String name,
        String lastName,
        String address,
        Birthday birthday,
        Email email,
        Salary baseSalary
) {

    public CreateUserCommand {
        ValidationResult vr = new ValidationResult();
        validateCommand(name, lastName, address, birthday, email, baseSalary, vr);

        if (vr.hasErrors()) {
            throw new DomainValidationException(vr.getErrors());
        }
    }

    private static void validateCommand(String name, String lastName, String address,
                                        Birthday birthday, Email email, Salary baseSalary,
                                        ValidationResult vr) {
        validateName(name, vr);
        validateLastName(lastName, vr);
        validateAddress(address, vr);
        validateBirthday(birthday, vr);
        validateEmail(email, vr);
        validateSalary(baseSalary, vr);
    }

    private static void validateName(String name, ValidationResult vr) {
        if (name == null || name.trim().isEmpty()) {
            vr.addError("El nombre es obligatorio");
            return;
        }
        if (name.trim().length() < 2) {
            vr.addError("El nombre debe tener al menos 2 caracteres");
        }
        if (name.trim().length() > 50) {
            vr.addError("El nombre no puede tener más de 50 caracteres");
        }
    }

    private static void validateLastName(String lastName, ValidationResult vr) {
        if (lastName == null || lastName.trim().isEmpty()) {
            vr.addError("El apellido es obligatorio");
            return;
        }
        if (lastName.trim().length() < 2) {
            vr.addError("El apellido debe tener al menos 2 caracteres");
        }
        if (lastName.trim().length() > 50) {
            vr.addError("El apellido no puede tener más de 50 caracteres");
        }
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

    private static void validateBirthday(Birthday birthday, ValidationResult vr) {
        if (birthday == null) {
            vr.addError("La fecha de nacimiento es obligatoria");
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