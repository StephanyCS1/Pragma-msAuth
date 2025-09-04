package co.com.crediya.api.config;

import co.com.crediya.api.dto.GeneralResponse;
import co.com.crediya.api.security.JwtService;
import co.com.crediya.model.user.enums.RolName;
import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtService jwtService) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(reg -> reg
                        .pathMatchers("/api/v1/login").permitAll()
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs/swagger-config",
                                "/webjars/**"
                        ).permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/usuarios/**")
                        .hasAnyRole(RolName.ADMIN.name(), RolName.ASESOR.name())
                        .pathMatchers("/api/v1/usuarios/**")
                        .hasAnyRole(RolName.ADMIN.name(), RolName.ASESOR.name())

                        .pathMatchers("/api/v1/solicitud/**")
                        .hasAnyRole(RolName.ADMIN.name(), RolName.ASESOR.name(), RolName.USER.name())
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) ->
                                writeJson(exchange, HttpStatus.UNAUTHORIZED, "No autenticado"))
                        .accessDeniedHandler((exchange, e) ->
                                writeJson(exchange, HttpStatus.FORBIDDEN, "Acceso denegado"))
                )


                // Filtro simple para validar Bearer y poblar Authentication
                .addFilterAt((exchange, chain) -> bearerToAuth(exchange)
                                .flatMap(token -> authenticate(token, jwtService))
                                .flatMap(auth -> chain.filter(exchange)
                                        .contextWrite(ctx -> org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication(auth)))
                                .switchIfEmpty(chain.filter(exchange))
                        , SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }

    private Mono<String> bearerToAuth(ServerWebExchange exchange) {
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return Mono.empty();
        }
        return Mono.just(auth.substring(7));
    }

    private Mono<AbstractAuthenticationToken> authenticate(String token, JwtService jwtService) {
        try {
            var jws = jwtService.parseAndValidate(token);
            Claims claims = jws.getBody();
            String rol = claims.get("rol", String.class);
            var authorities = rol == null ? List.of() : List.of(new SimpleGrantedAuthority("ROLE_" + rol));

            var details = Map.copyOf(claims);

            var auth = new AbstractAuthenticationToken((Collection<? extends GrantedAuthority>) authorities) {
                @Override public Object getCredentials() { return token; }
                @Override public Object getPrincipal() { return claims.get("email"); }
            };
            auth.setAuthenticated(true);
            auth.setDetails(details);
            return Mono.just(auth);
        } catch (Exception e) {
            return Mono.error(new BadCredentialsException("Invalid JWT", e));
        }
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        var body = new GeneralResponse<>(status.value(), null, message);
        try {
            var json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsBytes(body);
            var buffer = response.bufferFactory().wrap(json);
            return response.writeWith(Mono.just(buffer));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}