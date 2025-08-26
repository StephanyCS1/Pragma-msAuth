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
                .hasMessage("La fecha de nacimiento no puede ser futura");
    }


    @Test
    @DisplayName("shouldThrowExceptionWhenBirthdayIsTooOld")
    void shouldThrowExceptionWhenBirthdayIsTooOld() {
        LocalDate tooOldDate = LocalDate.now().minusYears(121);
        assertThatThrownBy(() -> new Birthday(tooOldDate))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("no puede ser anterior a");
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

    @Test
    @DisplayName("shouldThrowExceptionWhenMultipleValidationErrors")
    void shouldThrowExceptionWhenMultipleValidationErrors() {
        LocalDate futureAndOldDate = LocalDate.now().plusYears(1);
        assertThatThrownBy(() -> new Birthday(futureAndOldDate))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    @DisplayName("shouldAcceptBirthdayExactlyOnBoundaryDates")
    void shouldAcceptBirthdayExactlyOnBoundaryDates() {
        LocalDate exactly120Years = LocalDate.now().minusYears(120);
        assertThatCode(() -> new Birthday(exactly120Years))
                .doesNotThrowAnyException();

        LocalDate today = LocalDate.now();
        assertThatCode(() -> new Birthday(today))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenOfMethodReceivesInvalidDate")
    void shouldThrowExceptionWhenOfMethodReceivesInvalidDate() {
        String invalidDate = "2025-13-50"; // Mes y día inválidos
        assertThatThrownBy(() -> Birthday.of(invalidDate))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("Formato de fecha inválido, use yyyy-MM-dd");
    }
}