package com.mini.timecapsule;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().version("v.1.0").title("time_capsule_doc").description("time capsule api descriptions");
        return new OpenAPI().info(info);
    }
}
