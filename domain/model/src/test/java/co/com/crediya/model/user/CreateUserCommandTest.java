package co.com.crediya.model.user;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.valueobjects.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CreateUserCommand Tests")
class CreateUserCommandTest {

    private Birthday validBirthday;
    private Email validEmail;
    private Salary validSalary;

    @BeforeEach
    void setUp() {
        validBirthday = new Birthday(LocalDate.of(1990, 5, 15));
        validEmail = new Email("test@example.com");
        validSalary = new Salary(new BigDecimal("3000000"));
    }

    @Test
    @DisplayName("shouldCreateCommandWhenAllValidParametersProvided")
    void shouldCreateCommandWhenAllValidParametersProvided() {
        String name = "Juan";
        String lastName = "Pérez";
        String address = "Calle 123 #45-67";

        CreateUserCommand command = new CreateUserCommand(name, lastName, address, validBirthday, validEmail, validSalary);
        assertThat(command).isNotNull();
        assertThat(command.name()).isEqualTo(name);
        assertThat(command.lastName()).isEqualTo(lastName);
        assertThat(command.address()).isEqualTo(address);
        assertThat(command.birthday()).isEqualTo(validBirthday);
        assertThat(command.email()).isEqualTo(validEmail);
        assertThat(command.baseSalary()).isEqualTo(validSalary);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("shouldThrowExceptionWhenNameIsNullOrBlank")
    void shouldThrowExceptionWhenNameIsNullOrBlank(String invalidName) {
        assertThatThrownBy(() -> new CreateUserCommand(invalidName, "Pérez", "Calle 123 #45-67", validBirthday, validEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("El nombre es obligatorio");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenNameIsTooShort")
    void shouldThrowExceptionWhenNameIsTooShort() {
        String shortName = "J";
        assertThatThrownBy(() -> new CreateUserCommand(shortName, "Pérez", "Calle 123 #45-67", validBirthday, validEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("El nombre debe tener al menos 2 caracteres");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenNameIsTooLong")
    void shouldThrowExceptionWhenNameIsTooLong() {
        String longName = "a".repeat(51);
        assertThatThrownBy(() -> new CreateUserCommand(longName, "Pérez", "Calle 123 #45-67", validBirthday, validEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("El nombre no puede tener más de 50 caracteres");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("shouldThrowExceptionWhenLastNameIsNullOrBlank")
    void shouldThrowExceptionWhenLastNameIsNullOrBlank(String invalidLastName) {
        assertThatThrownBy(() -> new CreateUserCommand("Juan", invalidLastName, "Calle 123 #45-67", validBirthday, validEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("El apellido es obligatorio");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenAddressIsTooShort")
    void shouldThrowExceptionWhenAddressIsTooShort() {
        String shortAddress = "Calle 1";
        assertThatThrownBy(() -> new CreateUserCommand("Juan", "Pérez", shortAddress, validBirthday, validEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("La dirección debe tener al menos 10 caracteres");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenAddressIsTooLong")
    void shouldThrowExceptionWhenAddressIsTooLong() {
        String longAddress = "a".repeat(201);
        assertThatThrownBy(() -> new CreateUserCommand("Juan", "Pérez", longAddress, validBirthday, validEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("La dirección no puede tener más de 200 caracteres");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenBirthdayIsNull")
    void shouldThrowExceptionWhenBirthdayIsNull() {
        Birthday nullBirthday = null;
        assertThatThrownBy(() -> new CreateUserCommand("Juan", "Pérez", "Calle 123 #45-67", nullBirthday, validEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("La fecha de nacimiento es obligatoria");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenEmailIsNull")
    void shouldThrowExceptionWhenEmailIsNull() {
        Email nullEmail = null;
        assertThatThrownBy(() -> new CreateUserCommand("Juan", "Pérez", "Calle 123 #45-67", validBirthday, nullEmail, validSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("El email es obligatorio");
    }
    @Test
    @DisplayName("shouldThrowExceptionWhenSalaryIsNull")
    void shouldThrowExceptionWhenSalaryIsNull() {
        // Given
        Salary nullSalary = null;
        assertThatThrownBy(() -> new EditUserCommand("Calle 123 #45-67", validEmail, nullSalary))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("El salario base es obligatorio");
    }

    @Test
    @DisplayName("shouldThrowExceptionWithMultipleErrorsWhenMultipleFieldsAreInvalid")
    void shouldThrowExceptionWithMultipleErrorsWhenMultipleFieldsAreInvalid() {
        String shortAddress = "Short";
        Email nullEmail = null;
        Salary nullSalary = null;
        assertThatThrownBy(() -> new EditUserCommand(shortAddress, nullEmail, nullSalary))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(exception -> {
                    DomainValidationException domainException = (DomainValidationException) exception;
                    assertThat(domainException.getErrors()).hasSize(3);
                    assertThat(domainException.getErrors()).contains(
                            "La dirección debe tener al menos 10 caracteres",
                            "El email es obligatorio",
                            "El salario base es obligatorio"
                    );
                });
    }

    @Test
    @DisplayName("shouldAcceptValidBoundaryAddress")
    void shouldAcceptValidBoundaryAddress() {
        String minValidAddress = "1234567890";
        assertThatCode(() -> new EditUserCommand(minValidAddress, validEmail, validSalary))
                .doesNotThrowAnyException();
    }
}
