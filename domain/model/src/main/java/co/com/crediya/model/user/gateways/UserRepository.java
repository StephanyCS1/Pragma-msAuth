package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.valueobjects.Email;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {
        // Crear usuario
        Mono<User> saveUser(User user);

        // Actualizar usuario
        Mono<User> updateUser(User user);

        // Obtener usuario por email
        Mono<User> findByEmail(Email email);

        // Obtener usuario por id
        Mono<User> findById(UUID id);


        // Obtener todos los usuarios
        Flux<User> findAll();

        // Eliminar usuario por el id
        Mono<Void> delete(UUID id);
        // Eliminar usuario por email
        Mono<Void> deleteByEmail(Email email);

        // validacion para email unico
        Mono<Boolean> existsByEmail(Email email);
        Mono<Boolean> existsByEmailAndId(Email email, UUID id);
}
