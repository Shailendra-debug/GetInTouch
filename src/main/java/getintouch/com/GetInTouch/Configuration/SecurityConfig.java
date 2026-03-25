package getintouch.com.GetInTouch.Configuration;

import getintouch.com.GetInTouch.Filter.JwtAuthFilter;
import getintouch.com.GetInTouch.security.CustomAccessDeniedHandler;
import getintouch.com.GetInTouch.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;// ✅ add this



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )


                .authorizeHttpRequests(auth -> auth
                // ✅ Auth APIs
                .requestMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()

                // ✅ Swagger
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml"
                ).permitAll()

                // ✅ Public APIs
                .requestMatchers(HttpMethod.GET, "/api/sliders/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/marquee/**").permitAll()

                // ✅ CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ Admin APIs
                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
        )

                // JWT Filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                // ✅ Google OAuth2 Login
        return http.build();
    }

}
