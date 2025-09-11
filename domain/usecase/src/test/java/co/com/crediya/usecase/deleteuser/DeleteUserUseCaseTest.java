package co.com.crediya.usecase.deleteuser;


import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserUseCase Tests")
class DeleteUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    private User existingUser;
    private Email userEmail;

    @BeforeEach
    void setUp() {
        userEmail = new Email("test.user@example.com");
        existingUser = User.builder().email(userEmail).build();
    }

    @Test
    @DisplayName("shouldDeleteUserWhenEmailExists")
    void deleteUser_ShouldSucceed_WhenUserExists() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Mono.just(existingUser));
        when(userRepository.deleteByEmail(any(Email.class))).thenReturn(Mono.empty());

        StepVerifier.create(deleteUserUseCase.deleteUser(userEmail.value()))
                .verifyComplete();

        verify(userRepository, times(1)).deleteByEmail(any(Email.class));
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenEmailNotExists")
    void deleteUser_ShouldReturnError_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Mono.empty());

        StepVerifier.create(deleteUserUseCase.deleteUser("nonexistent@example.com"))
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("El email no existe."))
                .verify();
    }
}