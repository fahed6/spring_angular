package com.javauit.autoecole;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS is handled exclusively by SecurityConfig.corsConfigurationSource().
// Having a second CORS configuration here would produce duplicate
// Access-Control-Allow-Origin headers, which browsers reject with a 403.
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // No addCorsMappings — Spring Security's CorsFilter is the single CORS source.
}
