package co.com.crediya.model.user;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.valueobjects.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {
    @Test
    void shouldCreate_whenFormatIsValid() {
        new Email("tefis@crediya.com");
    }

    
    @Test void shouldFail_whenFormatIsInvalid() {
        assertThrows(DomainValidationException.class, () -> new Email("bad-email"));
    }
}