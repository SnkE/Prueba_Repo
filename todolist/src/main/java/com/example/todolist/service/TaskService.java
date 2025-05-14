package com.example.todolist.service;

import com.example.todolist.model.Task;
import com.example.todolist.service.UserService;
import com.example.todolist.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequestMapping("/api/tasks")
public class TaskService {
    private final TaskRepository taskRepo;
    private final UserService userService;  

    public TaskService(TaskRepository taskRepo, UserService userService) {
        this.taskRepo = taskRepo;
        this.userService = userService;
    }

    public Task save(Task task) {
        return taskRepo.save(task);
    }

    public Optional<Task> findById(Long id) {
        return taskRepo.findById(id);
    }

    public List<Task> findAll() {
        return taskRepo.findAll();
    }

    public List<Task> findByUserId(Long userId) {
        if (userService.findById(userId).isPresent()) {
            return taskRepo.findByUserId(userId);
        }
        return List.of();
    }
    

    public void deleteById(Long id) {
        taskRepo.deleteById(id);
    }

    public List<Task> findByUsername(String username) {
        return userService.findByUsername(username)
                          .map(u -> taskRepo.findByUserId(u.getId()))
                          .orElse(List.of());
    }
    
}
