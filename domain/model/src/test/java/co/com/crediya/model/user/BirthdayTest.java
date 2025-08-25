package co.com.crediya.model.user;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.valueobjects.Birthday;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Birthday Value Object Tests")
class BirthdayTest {

    @Test
    @DisplayName("shouldCreateBirthdayWhenValidDateProvided")
    void shouldCreateBirthdayWhenValidDateProvided() {
        LocalDate validDate = LocalDate.of(1990, 5, 15);
        Birthday birthday = new Birthday(validDate);
        assertThat(birthday.value()).isEqualTo(validDate);
    }

    @Test
    @DisplayName("shouldCreateBirthdayFromIsoStringWhenValidFormatProvided")
    void shouldCreateBirthdayFromIsoStringWhenValidFormatProvided() {
        String validIsoDate = "1990-05-15";
        Birthday birthday = Birthday.of(validIsoDate);
        assertThat(birthday.value()).isEqualTo(LocalDate.of(1990, 5, 15));
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenBirthdayIsNull")
    void shouldThrowExceptionWhenBirthdayIsNull() {
        LocalDate nullDate = null;
        assertThatThrownBy(() -> new Birthday(nullDate))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("La fecha de nacimiento es obligatoria");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenBirthdayIsInFuture")
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertThatThrownBy(() -> new Birthday(futureDate))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("La fecha debe ser pasada");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenBirthdayIsTooOld")
    void shouldThrowExceptionWhenBirthdayIsTooOld() {
        LocalDate tooOldDate = LocalDate.now().minusYears(121);
        assertThatThrownBy(() -> new Birthday(tooOldDate))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("La fecha debe ser mayor o igual a");
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenInvalidIsoDateFormat")
    void shouldThrowExceptionWhenInvalidIsoDateFormat() {
        String invalidFormat = "15/05/1990";
        assertThatThrownBy(() -> Birthday.of(invalidFormat))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("Formato de fecha inválido, use yyyy-MM-dd");
    }

    @Test
    @DisplayName("shouldAcceptBirthdayExactly120YearsAgo")
    void shouldAcceptBirthdayExactly120YearsAgo() {
        LocalDate exactly120YearsAgo = LocalDate.now().minusYears(120);

        assertThatCode(() -> new Birthday(exactly120YearsAgo))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("shouldAcceptBirthdayToday")
    void shouldAcceptBirthdayToday() {
        LocalDate today = LocalDate.now();

        assertThatCode(() -> new Birthday(today))
                .doesNotThrowAnyException();
    }
}