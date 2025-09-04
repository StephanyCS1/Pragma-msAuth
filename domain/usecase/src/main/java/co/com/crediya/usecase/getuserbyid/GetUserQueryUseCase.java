package co.com.crediya.usecase.getuserbyid;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetUserQueryUseCase {

    private final UserRepository userRepository;

    public Mono<User> findUserByEmail(String email) {
        Email mail = new Email(email);
        return userRepository.findByEmail(mail)
                .switchIfEmpty(Mono.error(new UserNotFoundException("El email no existe.")));
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll()
                .switchIfEmpty(Mono.error(new DomainValidationException("No hay registros.")));
    }

}
