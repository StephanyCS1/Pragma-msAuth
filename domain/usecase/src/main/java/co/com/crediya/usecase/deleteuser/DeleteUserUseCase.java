package co.com.crediya.usecase.deleteuser;

import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteUserUseCase {
    private final UserRepository userRepository;

    public Mono<Void> deleteUser(String email) {
        Email mail = new Email(email);
        return userRepository.findByEmail(mail)
                .switchIfEmpty(Mono.error(new UserNotFoundException("El email no existe.")))
                .flatMap(user -> userRepository.deleteByEmail(mail));
    }
}
