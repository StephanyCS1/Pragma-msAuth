package co.com.crediya.api.dto;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record EditUserRequest(
        @NotBlank(message = "La dirección es requerida")
        String address,

        @NotBlank(message = "El email es requerido")
        @Email(message = "Formato de email inválido")
        String email,

        @NotNull(message = "El salario es requerido")
        @Positive(message = "El salario debe ser mayor a 0")
        BigDecimal baseSalary
) {

}
