package co.com.crediya.model.user.valueobjects;

import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;

import java.math.BigDecimal;

public record Salary(BigDecimal amount) {
    private static final BigDecimal MIN = new BigDecimal("0");
    private static final BigDecimal MAX = new BigDecimal("15000000");

    public Salary {
        ValidationResult vr = new ValidationResult();
        validate(amount, vr);
        if (vr.hasErrors()) {
            throw new DomainValidationException(String.join("; ", vr.getErrors()));
        }
    }

    public static void validate(BigDecimal amount, ValidationResult vr) {
        if (amount == null) { vr.addError("El salario es obligatorio"); return; }
        if (amount.compareTo(MIN) < 0 || amount.compareTo(MAX) > 0) {
            vr.addError("El salario debe estar entre 0 y 15000000");
        }
    }
}