package com.example.taxone.seeder;

import com.example.taxone.entity.User;
import com.example.taxone.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    private final Faker faker = new Faker();

    public void seedUsers(){
        if(userRepository.count() > 0) {
            log.info("User seeder skipped");
            return;
        }

        List<User> users = new ArrayList<>();

        for(int i = 0; i < 300; i++) {
            User user = User
                    .builder()
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .email(faker.internet().safeEmailAddress())
                    .emailVerified(false)
                    .isActive(true)
                    .password(passwordEncoder.encode("password"))
                    .phoneNumber(faker.phoneNumber().phoneNumber())
                    .build();
            users.add(user);
        }

        userRepository.saveAll(users);
        log.info("user seeder run successfully!");
    }
}
