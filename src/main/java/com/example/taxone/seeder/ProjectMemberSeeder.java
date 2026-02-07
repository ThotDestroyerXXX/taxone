package com.example.taxone.seeder;


import com.example.taxone.entity.Project;
import com.example.taxone.entity.ProjectMember;
import com.example.taxone.entity.WorkspaceMember;
import com.example.taxone.repository.*;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ProjectMemberSeeder {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int KEY_LENGTH = 4;

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private static final Logger log = LoggerFactory.getLogger(ProjectMemberSeeder.class);

    private final Faker faker = new Faker();
    Random random = new Random();

    public void seedProjectMembers() {

        if(projectMemberRepository.count() > 0) {
            log.info("projectMember seeder skipped");
            return;
        }

        List<Project> projects = projectRepository.findAll();
        List<ProjectMember.ProjectMemberType> types = new ArrayList<>();
        types.add(ProjectMember.ProjectMemberType.CONTRIBUTOR);
        types.add(ProjectMember.ProjectMemberType.VIEWER);

        List<ProjectMember> members = new ArrayList<>();

        for(Project project : projects){
            List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findAllByWorkspaceId(project.getWorkspace().getId());

            for(int i = 0; i < 20; i++) {
                Collections.shuffle(workspaceMembers);
                WorkspaceMember member =  workspaceMembers.get(0);

                ProjectMember projectMember = ProjectMember
                        .builder()
                        .memberType(member.getUser().getId()
                                .equals(project.getOwner().getId())
                                ? ProjectMember.ProjectMemberType.PROJECT_LEAD :
                                types.get(random.nextInt(types.size())))
                        .project(project)
                        .user(member.getUser())
                        .build();

                members.add(projectMember);
            }
        }
        projectMemberRepository.saveAll(members);
        log.info("projectMember seeder run successfully!");
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
