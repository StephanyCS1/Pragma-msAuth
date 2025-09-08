package co.com.crediya.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserResponse(
        String id,
        String name,
        String lastName,
        LocalDate birthday,
        String address,
        String email,
        BigDecimal baseSalary,
        String identification,
        String rol
) {
}
