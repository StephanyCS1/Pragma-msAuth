package co.com.crediya.model.user;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.valueobjects.Salary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Salary Value Object Tests")
class SalaryTest {

    @Test
    @DisplayName("shouldCreateSalaryWhenValidAmountProvided")
    void shouldCreateSalaryWhenValidAmountProvided() {
        BigDecimal validAmount = new BigDecimal("5000000");
        Salary salary = new Salary(validAmount);
        assertThat(salary.amount()).isEqualTo(validAmount);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1000000", "15000000"})
    @DisplayName("shouldCreateSalaryWhenValidRangeProvided")
    void shouldCreateSalaryWhenValidRangeProvided(String validAmount) {
        BigDecimal amount = new BigDecimal(validAmount);
        assertThatCode(() -> new Salary(amount))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenSalaryIsNull")
    void shouldThrowExceptionWhenSalaryIsNull() {
        BigDecimal nullAmount = null;
        assertThatThrownBy(() -> new Salary(nullAmount))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("El salario es obligatorio");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenSalaryIsNegative")
    void shouldThrowExceptionWhenSalaryIsNegative() {
        BigDecimal negativeAmount = new BigDecimal("-1000");
        assertThatThrownBy(() -> new Salary(negativeAmount))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("El salario debe estar entre 0 y 15000000");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenSalaryExceedsMaximum")
    void shouldThrowExceptionWhenSalaryExceedsMaximum() {
        BigDecimal excessiveAmount = new BigDecimal("15000001");

        assertThatThrownBy(() -> new Salary(excessiveAmount))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("El salario debe estar entre 0 y 15000000");
    }
}