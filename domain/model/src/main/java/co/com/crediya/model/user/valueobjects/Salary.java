package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.math.BigDecimal;

public record Salary(BigDecimal amount) {
    private static final BigDecimal MIN = new BigDecimal("0");
    private static final BigDecimal MAX = new BigDecimal("15000000");

    public Salary {
        validate(amount);

    }

    public static void validate(BigDecimal amount) {
        if (amount.compareTo(MIN) < 0 || amount.compareTo(MAX) > 0) {
            throw new DomainValidationException("El salario debe estar entre 0 y 15000000");
        }
    }
}