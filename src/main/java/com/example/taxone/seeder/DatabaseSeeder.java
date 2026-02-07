package com.example.taxone.seeder;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {

    private final UserSeeder  userSeeder;
    private final TaskSeeder  taskSeeder;
    private final WorkspaceSeeder  workspaceSeeder;
    private final WorkspaceMemberSeeder  workspaceMemberSeeder;
    private final ProjectSeeder      projectSeeder;
    private final ProjectMemberSeeder    projectMemberSeeder;
    private final LabelSeeder        labelSeeder;
    private final TaskLabelSeeder       taskLabelSeeder;

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userSeeder.seedUsers();
        workspaceSeeder.seedWorkspaces();
        workspaceMemberSeeder.seedWorkspaceMembers();
        projectSeeder.seedProjects();
        projectMemberSeeder.seedProjectMembers();
        taskSeeder.seedTasks();
        labelSeeder.seedLabels();
        taskLabelSeeder.seedTaskLabels();

        log.info("All seeder Successfully run!");
    }
}
