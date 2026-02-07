package com.example.taxone.seeder;


import com.example.taxone.entity.Project;
import com.example.taxone.entity.Workspace;
import com.example.taxone.entity.WorkspaceMember;
import com.example.taxone.repository.ProjectRepository;
import com.example.taxone.repository.WorkspaceMemberRepository;
import com.example.taxone.repository.WorkspaceRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ProjectSeeder {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int KEY_LENGTH = 4;

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    private static final Logger log = LoggerFactory.getLogger(ProjectSeeder.class);

    private final Faker faker = new Faker();
    Random random = new Random();

    public void seedProjects() {

        if(projectRepository.count() > 0) {
            log.info("project seeder skipped");
            return;
        }

        List<Workspace> workspaces = workspaceRepository.findAll();
        List<WorkspaceMember.MemberType> memberTypes = new ArrayList<>();
        memberTypes.add(WorkspaceMember.MemberType.OWNER);
        memberTypes.add(WorkspaceMember.MemberType.ADMIN);

        List<Project> projects = new ArrayList<>();

        for (Workspace workspace : workspaces) {
            List<WorkspaceMember> members = workspaceMemberRepository.
                    findByWorkspaceIdAndMemberTypeIn(workspace.getId(), memberTypes);

            for(int i = 0; i < 10; i++) {
                Collections.shuffle(members);
                Project project = Project
                        .builder()
                        .owner(members.get(0).getUser())
                        .projectKey(getTaskKey())
                        .startDate(faker.date().past(120, TimeUnit.DAYS))
                        .name(faker.lorem().sentence(4))
                        .workspace(workspace)
                        .priority(Project.ProjectPriority.values()[random.nextInt(Project.ProjectPriority.values().length)])
                        .endDate(faker.date().future(90, TimeUnit.DAYS))
                        .color(generateRandomHexColor())
                        .isPublic(random.nextBoolean())
                        .description(faker.lorem().paragraph(20))
                        .build();
                projects.add(project);
            }
        }
        projectRepository.saveAll(projects);
        log.info("project seeder run successfully!");
    }

    private String getTaskKey() {
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            key.append(CHARACTERS.charAt(randomIndex));
        }
        return key.toString();
    }

    private String generateRandomHexColor() {
        // Generate a random integer up to 0xFFFFFF (16777215)
        int nextInt = random.nextInt(0xffffff + 1);

        // Format the integer as a hexadecimal string with a '#' prefix and
        // ensuring 6 digits with leading zeros (%06x)
        return String.format("#%06x", nextInt);
    }
}
