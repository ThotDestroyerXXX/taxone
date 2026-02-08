package com.example.taxone.seeder;


import com.example.taxone.entity.*;
import com.example.taxone.repository.*;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TaskSeeder {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    private static final Logger log = LoggerFactory.getLogger(TaskSeeder.class);

    private final Faker faker = new Faker();
    Random random = new Random();

    public void seedTasks() {
        if(taskRepository.count() > 0) {
            log.info("task seeder skipped");
            return;
        }

        List<Project> projects = projectRepository.findAll();

        List<Task> tasks = new ArrayList<>();

        for(Project project : projects){
            List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(project.getId());

            for(int i = 0; i < 5; i++) {
                List<ProjectMember> qualified = new ArrayList<>(projectMembers.stream().filter(member ->
                                member.getMemberType()
                                        .equals(ProjectMember.ProjectMemberType.PROJECT_LEAD) ||
                                        member.getMemberType().equals(ProjectMember.ProjectMemberType.CONTRIBUTOR))
                        .toList());
                Collections.shuffle(qualified);
                Collections.shuffle(projectMembers);
                List<User> users = new ArrayList<>();
                users.add(projectMembers.get(0).getUser());
                users.add(projectMembers.get(1).getUser());
                users.add(projectMembers.get(2).getUser());
                Task task = Task
                        .builder()
                        .taskKey(project.getProjectKey() + "-00" + i)
                        .assignees(users)
                        .description(faker.lorem().paragraph(20))
                        .project(project)
                        .dueDate(faker.date().future(45, TimeUnit.DAYS))
                        .estimatedHours(300)
                        .priority(Task.TaskPriority.values()[random.nextInt(Task.TaskPriority.values().length)])
                        .reporter(qualified.get(0).getUser())
                        .title(faker.lorem().sentence(4))
                        .build();

                tasks.add(task);
            }
        }
        taskRepository.saveAll(tasks);
        log.info("task seeder run successfully!");
    }
}
