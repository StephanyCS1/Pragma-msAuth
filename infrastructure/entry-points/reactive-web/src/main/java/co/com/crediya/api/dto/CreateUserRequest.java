package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateUserRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String name,

        @NotBlank(message = "El apellido es requerido")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        String lastName,

        @NotBlank(message = "La fecha de nacimiento es requerida")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Formato de fecha inválido, use yyyy-MM-dd")
        String birthday,

        @NotBlank(message = "La dirección es requerida")
        String address,

        @NotBlank(message = "El email es requerido")
        @Email(message = "Formato de email inválido")
        String email,

        @NotNull(message = "El salario es requerido")
        @Positive(message = "El salario debe ser mayor a 0")
        BigDecimal baseSalary,

        @NotBlank(message = "La identificacion es requerida")
        String identification,

        @NotBlank(message = "La contraseña es requerida")
        String password,

        @NotBlank(message = "El rol es requerido")
        String rol
) {

}
