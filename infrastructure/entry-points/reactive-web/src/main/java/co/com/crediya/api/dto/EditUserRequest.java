package co.com.crediya.api.dto;

import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.math.BigDecimal;

public record EditUserRequest(
        String address,
        String email,
        BigDecimal baseSalary
) {

}
