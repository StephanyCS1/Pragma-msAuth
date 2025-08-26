package co.com.crediya.usecase.createuser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase Tests")
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private CreateUserUseCase createUserUseCase;
    private CreateUserCommand validCommand;

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCase(userRepository);
        validCommand = new CreateUserCommand(
                "Juan",
                "Pérez",
                "Calle 123 #45-67",
                new Birthday(LocalDate.of(1990, 5, 15)),
                new Email("juan.perez@example.com"),
                new Salary(new BigDecimal("3000000"))
        );
    }

    @Test
    @DisplayName("shouldCreateUserWhenValidCommandAndEmailNotExists")
    void shouldCreateUserWhenValidCommandAndEmailNotExists() {
        User expectedUser = User.create(null, "Juan", "Pérez",
                new Birthday(LocalDate.of(1990, 5, 15)),
                "Calle 123 #45-67",
                new Email("juan.perez@example.com"),
                new Salary(new BigDecimal("3000000")));

        when(userRepository.existsByEmail(eq(validCommand.email()))).thenReturn(Mono.just(false));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(createUserUseCase.createUser(validCommand))
                .expectNext(expectedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenEmailAlreadyExists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(eq(validCommand.email()))).thenReturn(Mono.just(true));

        StepVerifier.create(createUserUseCase.createUser(validCommand))
                .expectError(DomainValidationException.class)
                .verify();
    }
}