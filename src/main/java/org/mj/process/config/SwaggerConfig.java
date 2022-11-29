package org.mj.process.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Malek Jabri");
        contact.setEmail("malek@jabri.eu");
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("ProcessMiningAccelerator").description("Management Company , person, document,....").contact(contact).version("1.0"));

    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("ProcessMining")
                .pathsToMatch("/api/core/**").build();
    }


}
