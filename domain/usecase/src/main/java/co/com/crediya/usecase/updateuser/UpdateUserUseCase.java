package co.com.crediya.usecase.updateuser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.common.ValidationResult;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.model.user.valueobjects.EditUserCommand;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.model.user.valueobjects.Salary;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> editUser(UUID userId, EditUserCommand editUserCommand) {
        ValidationResult validation = validateCommand(editUserCommand);
        if (validation.hasErrors()) {
            return Mono.error(new DomainValidationException(validation.getErrors()));
        }

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new DomainValidationException("Usuario no encontrado")))
                .flatMap(existing -> {
                    User updated = existing.withAddressEmailSalary(
                            editUserCommand.address(),
                            editUserCommand.email(),
                            editUserCommand.baseSalary()
                    );
                    return userRepository.saveUser(updated);
                });
    }

    public Mono<User> editUserByEmail(Email userId, EditUserCommand editUserCommand) {
        ValidationResult validation = validateCommand(editUserCommand);
        if (validation.hasErrors()) {
            return Mono.error(new DomainValidationException(validation.getErrors()));
        }

        return userRepository.findByEmail(userId)
                .switchIfEmpty(Mono.error(new DomainValidationException("Usuario no encontrado")))
                .flatMap(existing -> {
                    User updated = existing.withAddressEmailSalary(
                            editUserCommand.address(),
                            editUserCommand.email(),
                            editUserCommand.baseSalary()
                    );
                    return userRepository.saveUser(updated);
                });
    }


    private ValidationResult validateCommand(EditUserCommand command) {
        ValidationResult result = new ValidationResult();

        if (command.address() == null || command.address().isBlank()) {
            result.addError("La dirección es obligatoria");
        }
        return result;
    }

}
