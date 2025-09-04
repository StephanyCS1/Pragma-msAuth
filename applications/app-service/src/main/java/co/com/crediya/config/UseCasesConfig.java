package co.com.crediya.config;

import co.com.crediya.model.user.gateways.PasswordEncodePort;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.createuser.CreateUserUseCase;
import co.com.crediya.usecase.deleteuser.DeleteUserUseCase;
import co.com.crediya.usecase.getuserbyid.GetUserQueryUseCase;
import co.com.crediya.usecase.updateuser.UpdateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository,
                                               PasswordEncodePort passwordEncoderPort) {
        return new CreateUserUseCase(userRepository, passwordEncoderPort);
    }
    @Bean
    DeleteUserUseCase deleteUserUseCase(UserRepository userRepository) {
        return new DeleteUserUseCase(userRepository);
    }

    @Bean
    GetUserQueryUseCase getUserQueryUseCase(UserRepository userRepository) {
        return new GetUserQueryUseCase(userRepository);
    }

    @Bean
    UpdateUserUseCase updateUserUseCase(UserRepository userRepository) {
        return new UpdateUserUseCase(userRepository);
    }
}