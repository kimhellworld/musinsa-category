package com.musinsa.category.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("무신사 카테고리 API")
                .description("무신사 카테고리 관리 API 문서")
                .version("v1.0.0");

        return new OpenAPI()
                .info(info);
    }
}
