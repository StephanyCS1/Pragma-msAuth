package co.com.crediya.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import co.com.crediya.usecase.createuser.CreateUserUseCase;
import co.com.crediya.usecase.getuserbyid.GetUserQueryUseCase;
import co.com.crediya.usecase.updateuser.UpdateUserUseCase;
import co.com.crediya.usecase.deleteuser.DeleteUserUseCase;
import co.com.crediya.api.mapper.UserMapper;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;
    @MockitoBean private GetUserQueryUseCase getUserQueryUseCase;
    @MockitoBean private UpdateUserUseCase updateUserUseCase;
    @MockitoBean private DeleteUserUseCase deleteUserUseCase;
    @MockitoBean private UserMapper userMapper;

    @Test
    void testListenGETUseCaseV1() {
        webTestClient.get()
                .uri("/api/v1/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
    @Test
    void testListenGETUseCaseV2() {
        webTestClient.get()
                .uri("/api/v2/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETOtherUseCaseV1() {
        webTestClient.get()
                .uri("/api/v1/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
    @Test
    void testListenGETOtherUseCaseV2() {
        webTestClient.get()
                .uri("/api/v2/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenPOSTUseCaseV1() {
        webTestClient.post()
                .uri("/api/v1/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
    @Test
    void testListenPOSTUseCaseV2() {
        webTestClient.post()
                .uri("/api/v2/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
}
