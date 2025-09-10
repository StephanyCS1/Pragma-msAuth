package co.com.crediya.usecase.updateuser;

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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserUseCase Tests")
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private UpdateUserUseCase updateUserUseCase;
    private User existingUser;
    private EditUserCommand validEditCommand;

    @BeforeEach
    void setUp() {
        updateUserUseCase = new UpdateUserUseCase(userRepository);
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

        validEditCommand = new EditUserCommand(
                "Nueva Calle 456 #78-90",
                new Email("nuevo.email@example.com"),
                new Salary(new BigDecimal("4000000"))
        );
    }

    @Test
    @DisplayName("shouldUpdateUserByIdWhenUserExists")
    void shouldUpdateUserByIdWhenUserExists() {

        UUID userId = existingUser.getId();
        User updatedUser = existingUser.withAddressEmailSalary(
                validEditCommand.address(),
                validEditCommand.email(),
                validEditCommand.baseSalary(),
                existingUser.getIdentification()
        );

        when(userRepository.findById(userId)).thenReturn(Mono.just(existingUser));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(updatedUser));


        StepVerifier.create(updateUserUseCase.editUser(userId, validEditCommand))
                .expectNext(updatedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenUserNotFoundById")
    void shouldThrowExceptionWhenUserNotFoundById() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(updateUserUseCase.editUser(userId, validEditCommand))
                .expectError(DomainValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("shouldUpdateUserByEmailWhenUserExists")
    void shouldUpdateUserByEmailWhenUserExists() {

        Email email = existingUser.getEmail();
        User updatedUser = existingUser.withAddressEmailSalary(
                validEditCommand.address(),
                validEditCommand.email(),
                validEditCommand.baseSalary(),
                existingUser.getIdentification()
        );

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(existingUser));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(updatedUser));

        StepVerifier.create(updateUserUseCase.editUserByEmail(email, validEditCommand))
                .expectNext(updatedUser)
                .verifyComplete();
    }


}