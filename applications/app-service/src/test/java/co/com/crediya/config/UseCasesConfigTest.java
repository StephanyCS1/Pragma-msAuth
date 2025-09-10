package co.com.crediya.config;

import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.createuser.CreateUserUseCase;
import co.com.crediya.usecase.deleteuser.DeleteUserUseCase;
import co.com.crediya.usecase.getuserbyid.GetUserQueryUseCase;
import co.com.crediya.usecase.updateuser.UpdateUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            assertNotNull(context.getBean(CreateUserUseCase.class));
            assertNotNull(context.getBean(DeleteUserUseCase.class));
            assertNotNull(context.getBean(GetUserQueryUseCase.class));
            assertNotNull(context.getBean(UpdateUserUseCase.class));

        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }
}