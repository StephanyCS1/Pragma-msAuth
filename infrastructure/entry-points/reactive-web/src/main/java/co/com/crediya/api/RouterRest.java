package co.com.crediya.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterRest {

    private static final String API_V1_USUARIOS = "/api/v1/usuarios";

    @RouterOperations({
            @RouterOperation(
                    path = API_V1_USUARIOS,
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createUser",
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Crear nuevo usuario",
                            tags = {"Usuarios"},
                            requestBody = @RequestBody(
                                    description = "Datos del usuario a crear",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = co.com.crediya.api.dto.CreateUserRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_USUARIOS,
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getAllUsers",
                    operation = @Operation(
                            operationId = "getAllUsers",
                            summary = "Obtener todos los usuarios",
                            tags = {"Usuarios"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_USUARIOS,
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getUserByEmail",
                    operation = @Operation(
                            operationId = "getUserByEmail",
                            summary = "Buscar usuario por email",
                            tags = {"Usuarios"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "email",
                                            description = "Email del usuario",
                                            required = true,
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "string", format = "email")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                                    @ApiResponse(responseCode = "400", description = "Email requerido"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_USUARIOS + "/{id}",
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "updateUser",
                    operation = @Operation(
                            operationId = "updateUser",
                            summary = "Actualizar usuario por ID",
                            tags = {"Usuarios"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "id",
                                            description = "ID del usuario",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", format = "uuid")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Nuevos datos del usuario",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = co.com.crediya.api.dto.EditUserRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_USUARIOS + "/email/{email}",
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "updateUserByEmail",
                    operation = @Operation(
                            operationId = "updateUserByEmail",
                            summary = "Actualizar usuario por email",
                            tags = {"Usuarios"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "email",
                                            description = "Email del usuario",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", format = "email")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Nuevos datos del usuario",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = co.com.crediya.api.dto.EditUserRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = API_V1_USUARIOS + "/{email}",
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "deleteUser",
                    operation = @Operation(
                            operationId = "deleteUser",
                            summary = "Eliminar usuario",
                            tags = {"Usuarios"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "email",
                                            description = "Email del usuario a eliminar",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", format = "email")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Error de validación"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        final String USERS = API_V1_USUARIOS;

        return RouterFunctions
                .route(POST(USERS).and(accept(MediaType.APPLICATION_JSON)), handler::createUser)
                .andRoute(GET(USERS).and(queryParam("email", email -> !email.isBlank())), handler::getUserByEmail)
                .andRoute(GET(USERS), req -> handler.getAllUsers())
                .andRoute(PUT(USERS + "/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::updateUser)
                .andRoute(PUT(USERS + "/email/{email}").and(accept(MediaType.APPLICATION_JSON)), handler::updateUserByEmail)
                .andRoute(DELETE(USERS + "/email/{email}"), handler::deleteUser);
    }
}