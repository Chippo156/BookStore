package com.book.book_store.configuration;

import com.book.book_store.common.UserType;
import com.book.book_store.model.Role;
import com.book.book_store.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "InitApp")
public class InitApp {
    private final RoleRepository roleRepository;



    @Bean
    @ConditionalOnProperty(prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner() {
        log.info("Initializing application.....");

        return args -> {
            Optional<Role> role = roleRepository.findByName(String.valueOf(UserType.ADMIN));
            if(role.isEmpty()){
                roleRepository.save(Role.builder()
                        .name(String.valueOf(UserType.ADMIN))
                        .description("Admin role")
                        .build());
            }
            role = roleRepository.findByName(String.valueOf(UserType.USER));
            if(role.isEmpty()){
                roleRepository.save(Role.builder()
                        .name(String.valueOf(UserType.USER))
                        .description("User role")
                        .build());
            }
            log.info("Application initialization completed .....");
        };
    }

}
