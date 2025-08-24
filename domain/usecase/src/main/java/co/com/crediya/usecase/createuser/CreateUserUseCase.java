package co.com.crediya.usecase.createuser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.CreateUserCommand;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> createUser(CreateUserCommand createUserCommand) {

        ValidationResult validation = validateCommand(createUserCommand);
        if (validation.hasErrors()) {
            return Mono.error(new DomainValidationException(validation.getErrors()));
        }

        return userRepository.existsByEmail(createUserCommand.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DomainValidationException("El email ya está registrado"));
                    }
                    return createAndSaveUser(createUserCommand);
                });
    }

    private Mono<User> createAndSaveUser(CreateUserCommand command) {
        User user = User.create(
                null,
                command.name(),
                command.lastName(),
                command.birthday(),
                command.address(),
                command.email(),
                command.baseSalary()
        );
        System.out.println("Creating user "+ user.toString());
        return userRepository.saveUser(user);
    }

    private ValidationResult validateCommand(CreateUserCommand command) {
        ValidationResult result = new ValidationResult();

        if (command.name() == null || command.name().isBlank()) {
            result.addError("El nombre es obligatorio");
        }
        if (command.lastName() == null || command.lastName().isBlank()) {
            result.addError("El apellido es obligatorio");
        }
        if (command.address() == null || command.address().isBlank()) {
            result.addError("La dirección es obligatoria");
        }

        return result;
    }

}
