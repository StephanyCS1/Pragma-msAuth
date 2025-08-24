package co.com.crediya.api.dto;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import java.math.BigDecimal;

public record CreateUserRequest(
        String name,
        String lastName,
        String birthday,   // "yyyy-MM-dd"
        String address,
        String email,
        BigDecimal baseSalary
) {

}
