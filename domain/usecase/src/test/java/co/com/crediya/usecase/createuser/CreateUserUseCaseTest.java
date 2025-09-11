package co.com.crediya.usecase.createuser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.gateways.PasswordEncodePort;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.Birthday;
import co.com.crediya.model.user.valueobjects.CreateUserCommand;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.model.user.valueobjects.Salary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncodePort passwordEncoder;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private CreateUserCommand validCommand;
    private User user;

    @BeforeEach
    void setUp() {
        validCommand = new CreateUserCommand(
                "Juan", "Perez", "Calle 100 # 50-20, Bogota",
                new Birthday(LocalDate.now().minusYears(30)),
                new Email("juan.perez@example.com"),
                new Salary(new BigDecimal("2000000")),
                "10101010", "password123", "CLIENT"
        );
        user = User.builder().id(UUID.randomUUID()).email(new Email("juan.perez@example.com")).build();
    }

    @Test
    void createUser_ShouldReturnError_WhenUserEmailAlreadyExists() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(true));

        StepVerifier.create(createUserUseCase.createUser(validCommand))
                .expectErrorMatches(throwable -> throwable instanceof DomainValidationException &&
                        throwable.getMessage().contains("El email ya está registrado"))
                .verify();
    }

    @Test
    void createUser_ShouldReturnError_WhenRolIsInvalid() {
        CreateUserCommand invalidCommand = new CreateUserCommand(
                "Juan", "Perez", "Calle 100 # 50-20, Bogota",
                new Birthday(LocalDate.now().minusYears(30)),
                new Email("test@example.com"),
                new Salary(new BigDecimal("2000000")),
                "10101010", "password123", "INVALID_ROL"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));

        StepVerifier.create(createUserUseCase.createUser(invalidCommand))
                .expectErrorMatches(throwable -> throwable instanceof DomainValidationException &&
                        throwable.getMessage().contains("Rol inválido"))
                .verify();
    }
}