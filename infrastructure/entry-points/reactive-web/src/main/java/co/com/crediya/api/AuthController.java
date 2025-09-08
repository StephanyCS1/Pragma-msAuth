package co.com.crediya.api;

import co.com.crediya.api.dto.GeneralResponse;
import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.TokenResponse;
import co.com.crediya.api.security.AuthService;
import co.com.crediya.api.security.JwtService;
import co.com.crediya.model.user.valueobjects.Email;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para autenticación y emisión de tokens JWT")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(
            summary = "Autenticación de usuario",
            description = """
                    Autentica al usuario con email y password. 
                    En caso de éxito, devuelve un JWT con los claims del usuario.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login exitoso, token emitido",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales inválidas",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno",
                            content = @Content
                    )
            }
    )
    @PostMapping("login")
    public Mono<ResponseEntity<GeneralResponse<TokenResponse>>> login(@RequestBody LoginRequest req) {
        log.info("Intento de login: {}", req.email());
        return authService.authenticate(new Email(req.email()), req.password())
                .doOnNext(u -> log.debug("Usuario autenticado: {} ({})", u.getEmail(), u.getRol()))
                .map(u -> {
                    String jwt = jwtService.generateToken(u);
                    var data = new TokenResponse(jwt,
                            jwtService.getExpirationMinutes(),
                            "Bearer");
                    return ResponseEntity.ok(new GeneralResponse<>(200, data, null));
                });
    }
}