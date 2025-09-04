package co.com.crediya.api.security;


import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialNotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> authenticate(Email email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado")))
                .doOnNext(u -> log.debug("Auth attempt for {}", u.getEmail()))
                .flatMap(u -> passwordEncoder.matches(rawPassword, u.getPassword())
                        ? Mono.just(u)
                        : Mono.error(new CredentialNotFoundException("Credenciales inválidas")));
    }
}