package com.example.taxone.service.impl;

import com.example.taxone.repository.TaskRepository;
import com.example.taxone.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
}
