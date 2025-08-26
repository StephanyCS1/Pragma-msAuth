package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserRequest;
import co.com.crediya.api.dto.EditUserRequest;
import co.com.crediya.api.dto.GeneralResponse;
import co.com.crediya.api.mapper.UserMapper;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.usecase.createuser.CreateUserUseCase;
import co.com.crediya.usecase.deleteuser.DeleteUserUseCase;
import co.com.crediya.usecase.getuserbyid.GetUserQueryUseCase;
import co.com.crediya.usecase.updateuser.UpdateUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateUserUseCase createUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetUserQueryUseCase getUserQueryUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserMapper userMapper;

    // Registrar usuario (POST /api/v1/usuarios)
    public Mono<ServerResponse> createUser(CreateUserRequest createUserRequest) {
        log.info("POST /usuarios - Iniciando creación de usuario");

        return Mono.just(createUserRequest)
                .doOnNext(req -> log.debug("Payload recibido: {}", req))
                .doOnNext(req -> log.info("Procesando creación de usuario con email: {}", req.email()))
                .map(userMapper::toCommand)
                .doOnNext(command -> log.debug("Command creado exitosamente para email: {}", command.email().value()))
                .flatMap(command -> {
                    log.debug("Ejecutando CreateUserUseCase");
                    return createUserUseCase.createUser(command);
                })
                .flatMap(user -> {
                    log.info("Usuario creado exitosamente con id={} y email={}", user.getId(), user.getEmail().value());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.CREATED.value(),
                                    userMapper.toResponse(user),
                                    null));
                })
                .doOnSuccess(response -> log.info("Respuesta HTTP 201 enviada para creación de usuario"));
    }

    // Buscar usuarios (GET /api/v1/usuarios)
    public Mono<ServerResponse> getAllUsers() {
        log.info("GET /usuarios - Iniciando consulta de todos los usuarios");

        return getUserQueryUseCase.findAllUsers()
                .doOnNext(user -> log.debug("Usuario encontrado: id={}, email={}", user.getId(), user.getEmail().value()))
                .map(userMapper::toResponse)
                .collectList()
                .doOnNext(users -> log.info("Se encontraron {} usuarios", users.size()))
                .flatMap(users -> {
                    log.debug("Enviando respuesta con {} usuarios", users.size());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.OK.value(),
                                    users,
                                    null));
                })
                .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para consulta de usuarios"));
    }

    // Buscar usuario por email (GET /api/v1/usuarios?email=...)
    public Mono<ServerResponse> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email requerido");
        }

        log.info("GET /usuarios?email={} - Iniciando búsqueda por email", email);

        return getUserQueryUseCase.findUserByEmail(email)
                .doOnNext(user -> log.debug("Usuario encontrado: id={}, email={}", user.getId(), user.getEmail().value()))
                .map(userMapper::toResponse)
                .flatMap(user -> {
                    log.info("Usuario encontrado exitosamente con email: {}", email);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.OK.value(),
                                    user,
                                    null));
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado con email: " + email)))
                .doOnSuccess(response -> log.debug("Respuesta enviada para búsqueda por email: {}", email));
    }

    // Actualizar usuario (PUT /api/v1/usuarios/{id})
    public Mono<ServerResponse> updateUser(String id, EditUserRequest editUserRequest) {
        return Mono.fromCallable(() -> UUID.fromString(id))
                .flatMap(userId -> {
                    log.info("PUT /usuarios/{} - Iniciando actualización de usuario", userId);

                    return Mono.just(editUserRequest)
                            .doOnNext(req -> log.debug("Payload de actualización recibido para userId={}: {}", userId, req))
                            .map(userMapper::toCommand)
                            .doOnNext(command -> log.debug("EditCommand creado exitosamente para userId={}", userId))
                            .flatMap(command -> {
                                log.debug("Ejecutando UpdateUserUseCase para userId={}", userId);
                                return updateUserUseCase.editUser(userId, command);
                            })
                            .flatMap(user -> {
                                log.info("Usuario actualizado exitosamente: id={}, email={}", user.getId(), user.getEmail().value());
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new GeneralResponse<>(
                                                HttpStatus.OK.value(),
                                                userMapper.toResponse(user),
                                                null));
                            })
                            .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para actualización de usuario: {}", userId));
                });
    }

    // Actualizar usuario por email (PUT /api/v1/usuarios/email/{email})
    public Mono<ServerResponse> updateUserByEmail(String emailParam, EditUserRequest editUserRequest) {
        log.info("PUT /usuarios/email/{} - Iniciando actualización de usuario por email", emailParam);

        return Mono.fromCallable(() -> new Email(emailParam))
                .flatMap(validEmail ->
                        Mono.just(editUserRequest)
                                .doOnNext(req -> log.debug("Payload de actualización recibido para email={}: {}", emailParam, req))
                                .map(userMapper::toCommand)
                                .doOnNext(command -> log.debug("EditCommand creado exitosamente para email={}", emailParam))
                                .flatMap(command -> {
                                    log.debug("Ejecutando UpdateUserUseCase para email={}", emailParam);
                                    return updateUserUseCase.editUserByEmail(validEmail, command);
                                })
                                .flatMap(user -> {
                                    log.info("Usuario actualizado exitosamente por email: id={}, email={}", user.getId(), user.getEmail().value());
                                    return ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(new GeneralResponse<>(
                                                    HttpStatus.OK.value(),
                                                    userMapper.toResponse(user),
                                                    null));
                                })
                )
                .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para actualización por email: {}", emailParam));
    }

    // Eliminar usuario (DELETE /api/v1/usuarios/{email})
    public Mono<ServerResponse> deleteUser(String email) {
        log.info("DELETE /usuarios/{} - Iniciando eliminación de usuario", email);

        return deleteUserUseCase.deleteUser(email)
                .doOnNext(result -> log.debug("Usuario eliminado exitosamente: {}", email))
                .then(Mono.defer(() -> {
                    log.info("Usuario eliminado exitosamente: {}", email);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.OK.value(),
                                    Map.of("message", "Usuario eliminado exitosamente"),
                                    null));
                }))
                .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para eliminación: {}", email));
    }
}