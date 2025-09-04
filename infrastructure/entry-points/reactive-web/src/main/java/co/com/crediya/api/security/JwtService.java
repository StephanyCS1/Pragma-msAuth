package co.com.crediya.api.security;

import co.com.crediya.api.dto.JwtProperties;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.enums.RolName;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.expirationMinutes() * 60L);
        Optional<RolName> rolName = RolName.fromId(user.getRol());
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("name", user.getName());
        claims.put("lastName", user.getLastName());
        claims.put("email", user.getEmail());
        claims.put("baseSalary", user.getBaseSalary() != null ? user.getBaseSalary() : null);
        claims.put("identification", user.getIdentification());
        claims.put("rol", rolName.get().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail().value())
                .setIssuer(props.issuer())
                .setAudience(props.audience())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseAndValidate(String jwt) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(props.issuer())
                .requireAudience(props.audience())
                .build()
                .parseClaimsJws(jwt);
    }

    public static LocalDate getLocalDateClaim(Claims c, String name) {
        String v = c.get(name, String.class);
        return v == null ? null : LocalDate.parse(v);
    }
    public static BigDecimal getBigDecimalClaim(Claims c, String name) {
        String v = c.get(name, String.class);
        return v == null ? null : new BigDecimal(v);
    }
    public long getExpirationMinutes() {
        return props.expirationMinutes();
    }
}
