package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserRequest;
import co.com.crediya.api.dto.EditUserRequest;
import co.com.crediya.api.dto.GeneralResponse;
import co.com.crediya.api.mapper.UserMapper;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.usecase.createuser.CreateUserUseCase;
import co.com.crediya.usecase.deleteuser.DeleteUserUseCase;
import co.com.crediya.usecase.getuserbyid.GetUserQueryUseCase;
import co.com.crediya.usecase.updateuser.UpdateUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
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
    private static final String ERROR_KEY = "error";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Error interno del servidor";
    private static final String INVALID_JSON_MSG = "JSON inválido o tipos incorrectos";
    private static final String EMAIL_PARAM = "email";

    // Registrar usuario (POST /api/v1/usuarios)
    public Mono<ServerResponse> createUser(ServerRequest request) {
        log.info("POST /usuarios - Iniciando creación de usuario");

        return request.bodyToMono(CreateUserRequest.class)
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
                .doOnSuccess(response -> log.info("Respuesta HTTP 201 enviada para creación de usuario"))
                .onErrorResume(DomainValidationException.class, ex -> {
                    log.warn("Error de validación al crear usuario: {}", ex.getErrors());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, ex.getErrors())));
                })
                .onErrorResume(DecodingException.class, ex -> {
                    log.warn("Error de decodificación JSON en creación de usuario: {}", ex.getMessage());
                    String msg = INVALID_JSON_MSG;
                    Throwable cause = ex.getCause();
                    if (cause instanceof ValueInstantiationException vie && vie.getCause() != null) {
                        msg = vie.getCause().getMessage();
                    }
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, msg)));
                })
                .onErrorResume(ServerWebInputException.class, ex -> {
                    log.warn("Error de entrada web en creación de usuario: {}", ex.getReason());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, ex.getReason())));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error inesperado al crear usuario", ex);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    null,
                                    Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MSG)));
                });
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
                .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para consulta de usuarios"))
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error inesperado al obtener usuarios", ex);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    null,
                                    Map.of(ERROR_KEY, "Error al obtener usuarios")));
                });
    }

    // Buscar usuario por email (GET /api/v1/usuarios?email=...)
    public Mono<ServerResponse> getUserByEmail(ServerRequest request) {
        return request.queryParam(EMAIL_PARAM)
                .map(email -> {
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
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Usuario no encontrado con email: {}", email);
                                return ServerResponse.status(HttpStatus.NOT_FOUND)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new GeneralResponse<>(
                                                HttpStatus.NOT_FOUND.value(),
                                                null,
                                                Map.of(ERROR_KEY, "Usuario no encontrado")));
                            }))
                            .doOnSuccess(response -> log.debug("Respuesta enviada para búsqueda por email: {}", email))
                            .onErrorResume(Exception.class, ex -> {
                                log.error("Error inesperado al buscar usuario por email: {}", email, ex);
                                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new GeneralResponse<>(
                                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                null,
                                                Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MSG)));
                            });
                })
                .orElseGet(() -> {
                    log.warn("Solicitud sin parámetro email requerido");
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, "Email requerido")));
                });
    }

    // Actualizar usuario (PUT /api/v1/usuarios/{id})
    public Mono<ServerResponse> updateUser(ServerRequest request) {
        final String rawId = request.pathVariable("id");
        UUID userId;
        try {
            userId = UUID.fromString(rawId);
        } catch (IllegalArgumentException ex) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new GeneralResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            null,
                            Map.of(ERROR_KEY, "El id no es un UUID válido")));
        }

        log.info("PUT /usuarios/{} - Iniciando actualización de usuario", userId);

        return request.bodyToMono(EditUserRequest.class)
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
                .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para actualización de usuario: {}", userId))
                .onErrorResume(DomainValidationException.class, ex -> {
                    log.warn("Error de validación al actualizar usuario {}: {}", userId, ex.getErrors());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, ex.getErrors())));
                })
                .onErrorResume(DecodingException.class, ex -> {
                    log.warn("Error de decodificación JSON en actualización de usuario {}: {}", userId, ex.getMessage());
                    String msg = INVALID_JSON_MSG;
                    Throwable cause = ex.getCause();
                    if (cause instanceof ValueInstantiationException vie && vie.getCause() != null) {
                        msg = vie.getCause().getMessage();
                    }
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, msg)));
                })
                .onErrorResume(ServerWebInputException.class, ex -> {
                    log.warn("Error de entrada web en actualización de usuario {}: {}", userId, ex.getReason());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, ex.getReason())));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error inesperado al actualizar usuario: {}", userId, ex);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    null,
                                    Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MSG)));
                });
    }

    // Actualizar usuario por email (PUT /api/v1/usuarios/email/{email})
    public Mono<ServerResponse> updateUserByEmail(ServerRequest request) {
        String emailParam = request.pathVariable(EMAIL_PARAM);
        log.info("PUT /usuarios/email/{} - Iniciando actualización de usuario por email", emailParam);

        return Mono.fromCallable(() -> new Email(emailParam))
                .flatMap(validEmail ->
                        request.bodyToMono(EditUserRequest.class)
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
                .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para actualización por email: {}", emailParam))
                .onErrorResume(DomainValidationException.class, ex -> {
                    log.warn("Error de validación al actualizar usuario por email {}: {}", emailParam, ex.getErrors());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, ex.getErrors())));
                })
                .onErrorResume(DecodingException.class, ex -> {
                    log.warn("Error de decodificación JSON en actualización por email {}: {}", emailParam, ex.getMessage());
                    String msg = INVALID_JSON_MSG;
                    Throwable cause = ex.getCause();
                    if (cause instanceof ValueInstantiationException vie && vie.getCause() != null) {
                        msg = vie.getCause().getMessage();
                    }
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, msg)));
                })
                .onErrorResume(ServerWebInputException.class, ex -> {
                    log.warn("Error de entrada web en actualización por email {}: {}", emailParam, ex.getReason());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, ex.getReason())));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error inesperado al actualizar usuario por email: {}", emailParam, ex);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    null,
                                    Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MSG)));
                });
    }
    // Eliminar usuario (DELETE /api/v1/usuarios/{email})
    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        String email = request.pathVariable(EMAIL_PARAM);
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
                .doOnSuccess(response -> log.info("Respuesta HTTP 200 enviada para eliminación: {}", email))
                .onErrorResume(DomainValidationException.class, ex -> {
                    log.warn("Error de validación al eliminar usuario {}: {}", email, ex.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.BAD_REQUEST.value(),
                                    null,
                                    Map.of(ERROR_KEY, ex.getMessage())));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error inesperado al eliminar usuario: {}", email, ex);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new GeneralResponse<>(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    null,
                                    Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MSG)));
                });
    }
}