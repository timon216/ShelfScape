package com.shelfscape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().ignoringRequestMatchers("/login")  // Wyłącz CSRF dla /login
                .and()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home", "/login").permitAll()  // Pozwól na dostęp do stron publicznych
                        .anyRequest().authenticated()  // Inne strony wymagają logowania
                )
                .formLogin((form) -> form
                        .loginPage("/login")  // Strona logowania
                        .loginProcessingUrl("/login")  // Proces logowania
                        .defaultSuccessUrl("/hello", true)  // Domyślne przekierowanie po udanym logowaniu
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());  // Pozwól na wylogowanie

        return http.build();
    }



    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("user"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
