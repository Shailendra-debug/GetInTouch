package getintouch.com.GetInTouch.Configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.mail.search.SearchTerm;
import org.hibernate.internal.util.collections.LinkedIdentityHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.*;

@Configuration
public class SwaggerSecurityConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }


}
