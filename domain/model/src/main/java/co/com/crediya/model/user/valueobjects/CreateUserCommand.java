package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.exceptions.DomainValidationException;

public record CreateUserCommand(
        String name,
        String lastName,
        String address,
        Birthday birthday,
        Email email,
        Salary baseSalary,
        String identification,
        String password,
        String rol
) {

    public CreateUserCommand {
        validateCommand(name, lastName, address, birthday, email, baseSalary);
    }

    private static void validateCommand(String name, String lastName, String address,
                                        Birthday birthday, Email email, Salary baseSalary) {
        validateName(name);
        validateLastName(lastName);
        validateAddress(address);
        validateBirthday(birthday);
        validateEmail(email);
        validateSalary(baseSalary);
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidationException("El nombre es obligatorio");
        }
        if (name.trim().length() < 2) {
            throw new DomainValidationException("El nombre debe tener al menos 2 caracteres");
        }
        if (name.trim().length() > 50) {
            throw new DomainValidationException("El nombre no puede tener más de 50 caracteres");
        }
    }

    private static void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new DomainValidationException("El apellido es obligatorio");
        }
        if (lastName.trim().length() < 2) {
            throw new DomainValidationException("El apellido debe tener al menos 2 caracteres");
        }
        if (lastName.trim().length() > 50) {
            throw new DomainValidationException("El apellido no puede tener más de 50 caracteres");
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new DomainValidationException("La dirección es obligatoria");
        }
        if (address.trim().length() < 10) {
            throw new DomainValidationException("La dirección debe tener al menos 10 caracteres");
        }
        if (address.trim().length() > 200) {
            throw new DomainValidationException("La dirección no puede tener más de 200 caracteres");
        }
    }

    private static void validateBirthday(Birthday birthday) {
        if (birthday == null) {
            throw new DomainValidationException("La fecha de nacimiento es obligatoria");
        }
    }

    private static void validateEmail(Email email) {
        if (email == null) {
            throw new DomainValidationException("El email es obligatorio");
        }
    }

    private static void validateSalary(Salary baseSalary) {
        if (baseSalary == null) {
            throw new DomainValidationException("El salario base es obligatorio");
        }
    }

}