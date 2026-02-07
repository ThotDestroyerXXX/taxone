package com.example.taxone.seeder;

import com.example.taxone.entity.*;
import com.example.taxone.repository.*;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LabelSeeder {

    private final WorkspaceRepository workspaceRepository;
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;

    private static final Logger log = LoggerFactory.getLogger(LabelSeeder.class);

    private final Faker faker = new Faker();
    Random random = new Random();

    public void seedLabels() {
        if(labelRepository.count() > 0) {
            log.info("label seeder skipped");
            return;
        }

        List<Workspace> workspaces = workspaceRepository.findAll();
        List<Label> labels = new ArrayList<>();

        for(Workspace workspace : workspaces){
            int min = 4, max = 10;
            int count = random.nextInt(max - min + 1) + min;

            for(int i = 0; i < count; i++) {
                Label label = Label
                        .builder()
                        .color(generateRandomHexColor())
                        .description(faker.lorem().paragraph(20))
                        .name(faker.lorem().sentence(4))
                        .workspace(workspace)
                        .build();
                labels.add(label);
            }
        }
        labelRepository.saveAll(labels);
        log.info("label seeder run successfully!");
    }

    private String generateRandomHexColor() {
        // Generate a random integer up to 0xFFFFFF (16777215)
        int nextInt = random.nextInt(0xffffff + 1);

        // Format the integer as a hexadecimal string with a '#' prefix and
        // ensuring 6 digits with leading zeros (%06x)
        return String.format("#%06x", nextInt);
    }
}
