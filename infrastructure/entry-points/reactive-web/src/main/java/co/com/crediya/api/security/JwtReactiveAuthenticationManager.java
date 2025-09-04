package co.com.crediya.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtReactiveAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();
        try {
            Jws<io.jsonwebtoken.Claims> jws = jwtService.parseAndValidate(token);
            Claims claims = jws.getBody();

            String rol = claims.get("rol", String.class);
            var authorities = rol == null
                    ? List.of()
                    : List.of(new SimpleGrantedAuthority("ROLE_" + rol));

            Map<String, Object> details = Map.copyOf(claims);

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
}