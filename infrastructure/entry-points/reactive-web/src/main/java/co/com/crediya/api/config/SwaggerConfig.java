package co.com.crediya.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crediya - API de Gestión de Usuarios")
                        .version("1.0.0")
                        .description("""
                                API RESTful para la gestión de usuarios del sistema Crediya.
                                
                                Esta API permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) 
                                sobre los usuarios del sistema, incluyendo:
                                
                                - Registro de nuevos usuarios
                                - Consulta de usuarios (todos o por email)
                                - Actualización de información de usuarios
                                - Eliminación de usuarios del sistema
                                
                                Construida con Spring WebFlux siguiendo principios de Clean Architecture.
                                """)
                        .contact(new Contact()
                                .name("Equipo de Desarrollo Crediya")
                                .email("desarrollo@crediya.com")
                                .url("https://crediya.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://api-dev.crediya.com")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.crediya.com")
                                .description("Servidor de producción")
                ));
    }
}