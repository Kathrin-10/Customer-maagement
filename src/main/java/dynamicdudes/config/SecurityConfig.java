package dynamicdudes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import dynamicdudes.repository.CustomerRepository;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    UserDetailsService userDetailsService(
            CustomerRepository customerRepository) {

        return identifier -> {

            String value =
                    identifier == null
                            ? ""
                            : identifier.trim();

            return customerRepository
                    .findByUsernameIgnoreCase(value)
                    .or(() ->
                            customerRepository
                                    .findByEmailIgnoreCase(value)
                    )
                    .map(customer ->
                            User.withUsername(customer.getUsername())
                                    .password(customer.getPassword())
                                    .roles(customer.getRole())
                                    .build()
                    )
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found"
                            )
                    );
        };
    }


    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // ========================================================
                // CSRF
                // ========================================================

                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository
                                        .withHttpOnlyFalse()
                        )
                        .ignoringRequestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/enquiries",
                                "/api/contacts"
                        )
                )


                // ========================================================
                // AUTHORIZATION
                // ========================================================

                .authorizeHttpRequests(authorize -> authorize

                        // Public pages/resources
                        .requestMatchers(
                                "/",
                                "/home.html",
                                "/about.html",
                                "/contact.html",
                                "/services.html",
                                "/login",
                                "/login.html",
                                "/admin-login.html",
                                "/register.html",
                                "/css/**",
                                "/styles.css",
                                "/images/**",
                                "/api/auth/register",
                                "/api/enquiries",
                                "/api/auth/login",
                                "/api/auth/csrf"
                        ).permitAll()


                        // Public contact submission
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/contacts"
                        ).permitAll()


                        // =================================================
                        // ADMIN
                        // =================================================

                        .requestMatchers(
                                "/admin.html",
                                "/admin",
                                "/admin-customers.html",
                                "/admin-projects.html",
                                "/admin-reports.html",
                                "/admin-profile.html",
                                "/api/admin/**"
                        ).hasRole("ADMIN")


                        // =================================================
                        // CUSTOMER
                        // =================================================

                        .requestMatchers(
                                "/dashboard.html",
                                "/dashboard",
                                "/projects.html",
                                "/register-project.html",
                                "/project-details.html",
                                "/payment.html",
                                "/profile.html",
                                "/api/customer/**"
                        ).hasRole("CUSTOMER")


                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )


                // ========================================================
                // FORM LOGIN
                // ========================================================

                .formLogin(form -> form

                        .loginPage("/login")

                        .loginProcessingUrl(
                                "/api/auth/login"
                        )

                        .usernameParameter("username")

                        .successHandler(
                                authenticationSuccessHandler()
                        )

                        .failureHandler(
                                authenticationFailureHandler()
                        )

                        .permitAll()
                )


                // ========================================================
                // LOGOUT
                // ========================================================

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutRequestMatcher(
                                request ->
                                        "GET".equals(request.getMethod())
                                                && "/logout".equals(
                                                        request.getRequestURI()
                                                )
                        )

                        .invalidateHttpSession(true)

                        .clearAuthentication(true)

                        .deleteCookies("JSESSIONID")

                        .logoutSuccessUrl(
                                "/login.html"
                        )

                        .permitAll()
                );

        return http.build();
    }


    private AuthenticationSuccessHandler
    authenticationSuccessHandler() {

        return (request, response, authentication) ->
                response.setStatus(200);
    }


    private AuthenticationFailureHandler
    authenticationFailureHandler() {

        return (request, response, exception) ->
                response.sendError(
                        401,
                        "Invalid username or password"
                );
    }
}
