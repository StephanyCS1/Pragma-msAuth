package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.UserReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserReactiveRepositoryAdapter extends UserReactiveAdapterOperations<
        User,
        UserEntity,
        UUID,
        UserReactiveRepository
        > implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    @Transactional
    public Mono<User> saveUser(User user) {
        UserEntity entity = toEntity(user);
        return repository.save(entity).map(this::toDomain);
    }

    @Override
    @Transactional
    public Mono<User> updateUser(User user) {
        return repository.findById(user.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found: " + user.getId())))
                .flatMap(existing -> {
                    UserEntity e = toEntity(user);
                    e.setId(existing.getId());
                    return repository.save(e);
                })
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<User> findByEmail(Email email) {
        return repository.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<User> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }


    @Override
    @Transactional(readOnly = true)
    public Flux<User> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    @Override
    @Transactional
    public Mono<Void> delete(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional
    public Mono<Void> deleteByEmail(Email email) {
        return repository.deleteByEmail(email.value());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existsByEmailAndId(Email email, UUID id) {
        return repository.existsByEmailAndIdNot(email.value(), id);
    }

    private UserEntity toEntity(User user) {
        UserEntity e = new UserEntity();
        e.setId(user.getId() != null ? user.getId() : null);
        e.setName(user.getName());
        e.setLastName(user.getLastName());
        e.setBirthday(user.getBirthday().value());
        e.setAddress(user.getAddress());
        e.setEmail(user.getEmail().value());
        e.setBaseSalary(user.getBaseSalary().amount());
        e.setIdentification(user.getIdentification());
        e.setPassword(user.getPassword());
        e.setRol(user.getRol());
        return e;
    }

    private User toDomain(UserEntity e) {
        return new User(
                e.getId() != null ? e.getId() : null,
                e.getName(),
                e.getLastName(),
                new co.com.crediya.model.user.valueobjects.Birthday(e.getBirthday()),
                e.getAddress(),
                new co.com.crediya.model.user.valueobjects.Email(e.getEmail()),
                new co.com.crediya.model.user.valueobjects.Salary(e.getBaseSalary()),
                e.getIdentification(),
                e.getPassword(),
                e.getRol()
        );
    }
}
