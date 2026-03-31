package com.foodsocial.social_media_food.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Social media food API",
                description = "API of social media about food.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Tolkach Ivan",
                        email = "ivantolkach2005@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local backend server"
                ),
                @Server(
                        url = "http://localhost:3000",
                        description = "Local frontend server"
                )
        }
)
public class OpenApiConfig {

}
