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
public class TaskLabelSeeder {

    private final WorkspaceRepository workspaceRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;
    private final TaskLabelRepository taskLabelRepository;

    private static final Logger log = LoggerFactory.getLogger(TaskLabelSeeder.class);

    private final Faker faker = new Faker();
    Random random = new Random();

    public void seedTaskLabels() {
        if(taskLabelRepository.count() > 0) {
            log.info("taskLabel seeder skipped");
            return;
        }

        List<Workspace> workspaces = workspaceRepository.findAll();

        List<TaskLabel> taskLabels = new ArrayList<>();

        for(Workspace workspace : workspaces){
            int min = 1, max = 3;
            int count = random.nextInt(max - min + 1) + min;

            List<Project> projects = projectRepository.findAllByWorkspaceId(workspace.getId());
            List<Label> labels = labelRepository.findAllByWorkspaceId(workspace.getId());

            for(Project project : projects){
                List<Task> tasks = taskRepository.findAllByProjectId(project.getId());
                for(Task task : tasks){
                    Collections.shuffle(labels);
                    for(int i = 0; i < count; i++) {
                        TaskLabel taskLabel = TaskLabel
                                .builder()
                                .task(task)
                                .label(labels.get(i))
                                .addedBy(task.getReporter())
                                .build();
                        taskLabels.add(taskLabel);
                    }
                }
            }
        }

        taskLabelRepository.saveAll(taskLabels);
        log.info("taskLabel seeder run successfully!");
    }

}
