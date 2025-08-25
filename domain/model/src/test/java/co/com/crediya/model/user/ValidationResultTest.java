package co.com.crediya.model.user;

import co.com.crediya.model.user.common.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ValidationResult Tests")
class ValidationResultTest {

    @Test
    @DisplayName("shouldBeInitiallyValidWhenCreated")
    void shouldBeInitiallyValidWhenCreated() {
        ValidationResult vr = new ValidationResult();

        assertThat(vr.hasErrors()).isFalse();
        assertThat(vr.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("shouldAddSingleErrorSuccessfully")
    void shouldAddSingleErrorSuccessfully() {

        ValidationResult vr = new ValidationResult();
        String errorMessage = "Error message";

        vr.addError(errorMessage);

        assertThat(vr.hasErrors()).isTrue();
        assertThat(vr.getErrors()).hasSize(1);
        assertThat(vr.getErrors()).contains(errorMessage);
    }

    @Test
    @DisplayName("shouldAddMultipleErrorsSuccessfully")
    void shouldAddMultipleErrorsSuccessfully() {
        ValidationResult vr = new ValidationResult();
        String error1 = "Error 1";
        String error2 = "Error 2";

        vr.addError(error1);
        vr.addError(error2);

        assertThat(vr.hasErrors()).isTrue();
        assertThat(vr.getErrors()).hasSize(2);
        assertThat(vr.getErrors()).containsExactly(error1, error2);
    }

    @Test
    @DisplayName("shouldAddAllErrorsFromCollectionSuccessfully")
    void shouldAddAllErrorsFromCollectionSuccessfully() {

        ValidationResult vr = new ValidationResult();
        List<String> errors = Arrays.asList("Error 1", "Error 2", "Error 3");

        vr.addAll(errors);

        assertThat(vr.hasErrors()).isTrue();
        assertThat(vr.getErrors()).hasSize(3);
        assertThat(vr.getErrors()).containsExactlyElementsOf(errors);
    }

    @Test
    @DisplayName("shouldHandleEmptyCollectionGracefully")
    void shouldHandleEmptyCollectionGracefully() {

        ValidationResult vr = new ValidationResult();
        List<String> emptyErrors = Collections.emptyList();

        vr.addAll(emptyErrors);

        assertThat(vr.hasErrors()).isFalse();
        assertThat(vr.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("shouldReturnImmutableErrorsList")
    void shouldReturnImmutableErrorsList() {

        ValidationResult vr = new ValidationResult();
        vr.addError("Error");

        List<String> errors = vr.getErrors();

        assertThatThrownBy(() -> errors.add("New Error"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("shouldCombineIndividualAndCollectionErrors")
    void shouldCombineIndividualAndCollectionErrors() {

        ValidationResult vr = new ValidationResult();
        String individualError = "Individual Error";
        List<String> collectionErrors = Arrays.asList("Collection Error 1", "Collection Error 2");

        vr.addError(individualError);
        vr.addAll(collectionErrors);

        assertThat(vr.hasErrors()).isTrue();
        assertThat(vr.getErrors()).hasSize(3);
        assertThat(vr.getErrors()).containsExactly(
                individualError,
                "Collection Error 1",
                "Collection Error 2"
        );
    }
}