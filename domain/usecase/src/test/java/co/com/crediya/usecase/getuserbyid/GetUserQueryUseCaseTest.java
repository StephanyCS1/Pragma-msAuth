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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserQueryUseCase Tests")
class GetUserQueryUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserQueryUseCase getUserQueryUseCase;

    private User user;
    private Email userEmail;

    @BeforeEach
    void setUp() {
        userEmail = new Email("test.user@example.com");
        user = User.builder().email(userEmail).build();
    }

    @Test
    @DisplayName("shouldReturnUserWhenEmailExists")
    void findUserByEmail_ShouldSucceed() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Mono.just(user));

        StepVerifier.create(getUserQueryUseCase.findUserByEmail(userEmail.value()))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenEmailNotExists")
    void findUserByEmail_ShouldReturnError_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Mono.empty());

        StepVerifier.create(getUserQueryUseCase.findUserByEmail(userEmail.value()))
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("El email no existe."))
                .verify();
    }

    @Test
    @DisplayName("shouldReturnAllUsersWhenUsersExist")
    void findAllUsers_ShouldSucceed() {
        List<User> userList = List.of(user, new User());
        when(userRepository.findAll()).thenReturn(Flux.fromIterable(userList));

        StepVerifier.create(getUserQueryUseCase.findAllUsers())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenNoUsersExist")
    void findAllUsers_ShouldReturnError_WhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(getUserQueryUseCase.findAllUsers())
                .expectErrorMatches(throwable -> throwable instanceof DomainValidationException &&
                        throwable.getMessage().contains("No hay registros."))
                .verify();
    }
}
