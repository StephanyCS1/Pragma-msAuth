package co.com.crediya.model.user;

import co.com.crediya.model.user.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DomainValidationException Tests")
class DomainValidationExceptionTest {

    @Test
    @DisplayName("shouldCreateExceptionWithSingleMessage")
    void shouldCreateExceptionWithSingleMessage() {

        String errorMessage = "Single error message";

        DomainValidationException exception = new DomainValidationException(errorMessage);

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors()).contains(errorMessage);
    }

    @Test
    @DisplayName("shouldCreateExceptionWithMultipleMessages")
    void shouldCreateExceptionWithMultipleMessages() {

        List<String> errors = Arrays.asList("Error 1", "Error 2", "Error 3");

        DomainValidationException exception = new DomainValidationException(errors);

        assertThat(exception.getMessage()).isEqualTo("Error 1; Error 2; Error 3");
        assertThat(exception.getErrors()).hasSize(3);
        assertThat(exception.getErrors()).containsExactlyElementsOf(errors);
    }

    @Test
    @DisplayName("shouldReturnImmutableErrorsList")
    void shouldReturnImmutableErrorsList() {

        List<String> originalErrors = Arrays.asList("Error 1", "Error 2");
        DomainValidationException exception = new DomainValidationException(originalErrors);

        List<String> returnedErrors = exception.getErrors();

        assertThatThrownBy(() -> returnedErrors.add("New Error"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
    @Test
    @DisplayName("shouldNotBeAffectedByOriginalListModification")
    void shouldNotBeAffectedByOriginalListModification() {

        List<String> originalErrors = Arrays.asList("Error 1", "Error 2");
        DomainValidationException exception = new DomainValidationException(originalErrors);

        List<String> mutableList = new ArrayList<>(Arrays.asList("Error 1", "Error 2"));
        DomainValidationException exception2 = new DomainValidationException(mutableList);

        mutableList.add("New Error");

        assertThat(exception2.getErrors()).hasSize(2);
        assertThat(exception2.getErrors()).containsExactly("Error 1", "Error 2");
    }

    @Test
    @DisplayName("shouldHandleEmptyErrorsList")
    void shouldHandleEmptyErrorsList() {

        List<String> emptyErrors = Arrays.asList();

        DomainValidationException exception = new DomainValidationException(emptyErrors);

        assertThat(exception.getMessage()).isEmpty();
        assertThat(exception.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("shouldHandleNullMessage")
    void shouldHandleNullMessage() {

        String nullMessage = null;

        DomainValidationException exception = new DomainValidationException(nullMessage);

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors()).contains(nullMessage);
    }
}