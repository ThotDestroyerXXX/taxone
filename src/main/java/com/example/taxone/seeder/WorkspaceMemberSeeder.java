package com.example.taxone.seeder;


import com.example.taxone.entity.User;
import com.example.taxone.entity.Workspace;
import com.example.taxone.entity.WorkspaceMember;
import com.example.taxone.repository.UserRepository;
import com.example.taxone.repository.WorkspaceMemberRepository;
import com.example.taxone.repository.WorkspaceRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class WorkspaceMemberSeeder {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private static final Logger log = LoggerFactory.getLogger(WorkspaceMemberSeeder.class);

    private final Faker faker = new Faker();
    Random random = new Random();

    public void seedWorkspaceMembers() {
        if(workspaceMemberRepository.count() > 0) {
            log.info("workspaceMember seeder skipped");
            return;
        }

        List<Workspace> workspaces = workspaceRepository.findAll();

        List<WorkspaceMember.MemberType> types = new ArrayList<>();

        types.add(WorkspaceMember.MemberType.VIEWER);
        types.add(WorkspaceMember.MemberType.VIEWER);
        types.add(WorkspaceMember.MemberType.ADMIN);

        List<WorkspaceMember> members = new ArrayList<>();

        for(Workspace w : workspaces) {
            List<User> users = userRepository.findAll();
            WorkspaceMember owner = WorkspaceMember
                    .builder()
                    .workspace(w)
                    .memberType(WorkspaceMember.MemberType.OWNER)
                    .user(w.getOwner())
                    .build();
            workspaceMemberRepository.save(owner);

            for(int i = 0; i < 100; i++) {
                Collections.shuffle(users);
                WorkspaceMember member = WorkspaceMember
                        .builder()
                        .memberType(types.get(random.nextInt(types.size())))
                        .workspace(w)
                        .user(users.get(0))
                        .build();
                members.add(member);
            }
        }

        workspaceMemberRepository.saveAll(members);
        log.info("workspaceMember seeder run successfully!");
    }
}
