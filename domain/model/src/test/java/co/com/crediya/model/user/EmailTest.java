package co.com.crediya.model.user;

import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.valueobjects.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Email Value Object Tests")
class EmailTest {

    @Test
    @DisplayName("shouldCreateEmailWhenValidEmailProvided")
    void shouldCreateEmailWhenValidEmailProvided() {
        String validEmail = "test@example.com";
        Email email = new Email(validEmail);

        assertThat(email.value()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@domain.co",
            "user.name@domain.com",
            "user+tag@domain.org",
            "user_name@sub.domain.co.uk"
    })
    @DisplayName("shouldCreateEmailWhenValidFormatsProvided")
    void shouldCreateEmailWhenValidFormatsProvided(String validEmail) {
        assertThatCode(() -> new Email(validEmail))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenEmailIsNull")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        String nullEmail = null;

        // When & Then
        assertThatThrownBy(() -> new Email(nullEmail))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("El email es obligatorio");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "user@",
            "@domain.com",
            "user.domain.com",
            "user@domain",
            "user@.com",
            ""
    })
    @DisplayName("shouldThrowExceptionWhenInvalidEmailFormat")
    void shouldThrowExceptionWhenInvalidEmailFormat(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("El email no tiene el formato correcto");
    }

    @Test
    @DisplayName("shouldAddErrorToValidationResultWhenEmailIsNull")
    void shouldAddErrorToValidationResultWhenEmailIsNull() {
        ValidationResult vr = new ValidationResult();
        String nullEmail = null;

        Email.validate(nullEmail, vr);

        assertThat(vr.hasErrors()).isTrue();
        assertThat(vr.getErrors()).contains("El email es obligatorio");
    }

    @Test
    @DisplayName("shouldAddErrorToValidationResultWhenEmailFormatIsInvalid")
    void shouldAddErrorToValidationResultWhenEmailFormatIsInvalid() {
        ValidationResult vr = new ValidationResult();
        String invalidEmail = "invalid-email";

        Email.validate(invalidEmail, vr);

        assertThat(vr.hasErrors()).isTrue();
        assertThat(vr.getErrors()).contains("El email no tiene el formato correcto");
    }
}