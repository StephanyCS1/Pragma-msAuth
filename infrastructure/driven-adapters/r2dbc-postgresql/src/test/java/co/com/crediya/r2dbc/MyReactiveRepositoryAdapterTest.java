package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.model.user.valueobjects.Salary;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private UserEntity entity(String id, String email) {
        UserEntity e = new UserEntity();
        e.setId(UUID.fromString(id));
        e.setEmail(email);
        e.setName("Tefis");
        e.setLastName("Coder");
        e.setAddress("Calle 123");
        e.setBaseSalary(new BigDecimal("4200000"));
        e.setBirthday(LocalDate.parse("1998-05-10"));
        return e;
    }

    private User domain(String id, String email) {
        return new User(
                UUID.fromString(id),
                "Tefis",
                "Coder",
                null,
                "Calle 123",
                new Email(email),
                new Salary(new BigDecimal("4200000"))
        );
    }

    @Test
    void mustFindValueById() {
        var entity = entity("1", "tefis@crediya.com");
        var domain = domain("1", "tefis@crediya.com");

        when(repository.findById(UUID.fromString("1")).thenReturn(Mono.just(entity)));
        when(mapper.map(entity, User.class)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.findById(UUID.fromString("1"));

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().value().equals("tefis@crediya.com"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        var entity = entity("1", "tefis@crediya.com");
        var domain = domain("1", "tefis@crediya.com");

        when(repository.findAll()).thenReturn(Flux.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(domain);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().value().equals("tefis@crediya.com"))
                .verifyComplete();
    }

    @Test
    void mustFindByEmail() {
        var entity = entity("1", "tefis@crediya.com");
        var domain = domain("1", "tefis@crediya.com");

        when(repository.findByEmail("tefis@crediya.com")).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.findByEmail(new Email("tefis@crediya.com"));

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().value().equals("tefis@crediya.com"))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        var domain = domain("1", "tefis@crediya.com");
        var entity = entity("1", "tefis@crediya.com");

        when(mapper.map(domain, UserEntity.class)).thenReturn(entity);
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(domain);

        Mono<User> result = repositoryAdapter.save(domain);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().value().equals("tefis@crediya.com"))
                .verifyComplete();
    }
}
