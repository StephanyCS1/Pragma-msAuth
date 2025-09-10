package co.com.crediya.usecase.getuserbyid;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserQueryUseCase Tests")
class GetUserQueryUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private GetUserQueryUseCase getUserQueryUseCase;
    private User existingUser;

    @BeforeEach
    void setUp() {
        getUserQueryUseCase = new GetUserQueryUseCase(userRepository);
        existingUser = User.create(
                UUID.randomUUID(),
                "Juan",
                "Pérez",
                new Birthday(LocalDate.of(1990, 5, 15)),
                "Calle 123 #45-67",
                new Email("juan.perez@example.com"),
                new Salary(new BigDecimal("3000000")),
                "123456789"

        );
    }

    @Test
    @DisplayName("shouldReturnUserWhenEmailExists")
    void shouldReturnUserWhenEmailExists() {
        // Given
        String email = "juan.perez@example.com";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Mono.just(existingUser));

        // When & Then
        StepVerifier.create(getUserQueryUseCase.findUserByEmail(email))
                .expectNext(existingUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenEmailNotExists")
    void shouldThrowExceptionWhenEmailNotExists() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(getUserQueryUseCase.findUserByEmail(email))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("shouldReturnAllUsersWhenUsersExist")
    void shouldReturnAllUsersWhenUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.just(existingUser));

        // When & Then
        StepVerifier.create(getUserQueryUseCase.findAllUsers())
                .expectNext(existingUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenNoUsersExist")
    void shouldThrowExceptionWhenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(getUserQueryUseCase.findAllUsers())
                .expectError(DomainValidationException.class)
                .verify();
    }
}
