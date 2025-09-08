package co.com.crediya.api.dto;

import java.math.BigDecimal;

public record UserDto(String name,
                      String lastName,
                      String birthday,
                      String address,
                      String email,
                      BigDecimal baseSalary,
                      String identification,
                      String password,
                      String rol){
}
