package com.example.taxone.seeder;

import com.example.taxone.entity.User;
import com.example.taxone.entity.Workspace;
import com.example.taxone.repository.UserRepository;
import com.example.taxone.repository.WorkspaceRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkspaceSeeder {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository  userRepository;

    private static final Logger log = LoggerFactory.getLogger(WorkspaceSeeder.class);

    private final Faker faker = new Faker();

    public void seedWorkspaces() {
        if(workspaceRepository.count() > 0) {
            log.info("workspace seeder skipped");
            return;
        }

        List<Workspace> workspaces = new ArrayList<>();

        for(int i = 0; i < 20; i++) {
            List<User> users = userRepository.findAll();
            Collections.shuffle(users);
            Workspace workspace = Workspace
                    .builder()
                    .name(faker.lorem().sentence())
                    .description(faker.lorem().paragraph(20))
                    .isActive(true)
                    .slug(faker.lorem().sentence())
                    .owner(users.get(0))
                    .build();
            workspaces.add(workspace);
        }

        workspaceRepository.saveAll(workspaces);
        log.info("workspace seeder run successfully!");
    }
}
