package getintouch.com.GetInTouch.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

//@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GetInTouch API",
                version = "v1.0",
                description = "APIs for Home Page, Quiz CRUD, Users & Pdf Notes"
        )
)
public class SwaggerConfig {
}

