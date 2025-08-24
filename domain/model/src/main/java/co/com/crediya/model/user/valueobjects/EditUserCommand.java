package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.util.Objects;

public record EditUserCommand(
        String address,
        Email email,
        Salary baseSalary
) {

}