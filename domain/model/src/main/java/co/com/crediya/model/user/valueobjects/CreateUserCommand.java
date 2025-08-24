package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import lombok.NonNull;

import java.util.Objects;

public record CreateUserCommand(
        String name,
        String lastName,
        String address,
        Birthday birthday,
        Email email,
        Salary baseSalary
) {

}