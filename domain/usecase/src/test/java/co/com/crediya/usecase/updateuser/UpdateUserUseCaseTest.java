package co.com.crediya.usecase.updateuser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserUseCase Tests")
class UpdateUserUseCaseTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    private User existingUser;
    private EditUserCommand validCommand;
    private UUID userId;
    private Email userEmail;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userEmail = new Email("test.user@example.com");
        existingUser = User.builder()
                .id(userId)
                .email(userEmail)
                .identification("1010")
                .build();
        validCommand = new EditUserCommand(
                "Nueva Direccion de Prueba 123", new Email("new.email@example.com"), new Salary(new BigDecimal("3000000"))
        );
    }

    @Test
    void editUser_ShouldReturnError_WhenIdIsNotFound() {
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(updateUserUseCase.editUser(userId, validCommand))
                .expectErrorMatches(throwable -> throwable instanceof DomainValidationException &&
                        throwable.getMessage().contains("Usuario no encontrado"))
                .verify();
    }

    @Test
    void editUserByEmail_ShouldReturnError_WhenEmailIsNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Mono.empty());

        StepVerifier.create(updateUserUseCase.editUserByEmail(userEmail, validCommand))
                .expectErrorMatches(throwable -> throwable instanceof DomainValidationException &&
                        throwable.getMessage().contains("Usuario no encontrado"))
                .verify();
    }

}