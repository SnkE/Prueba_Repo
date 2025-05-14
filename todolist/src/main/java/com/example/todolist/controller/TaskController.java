package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService,
                          UserService userService,
                          TaskRepository taskRepository) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return taskService.findByUsername(userDetails.getUsername());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody Task task) 
    {
        var user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        task.setUser(user);
        Task saved = taskService.save(task);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long id,
                                           @RequestBody Task updatedTask) 
    {
        return taskService.findById(id)
            .filter(t -> t.getUser().getUsername().equals(userDetails.getUsername()))
            .map(t -> {
                t.setTitle(updatedTask.getTitle());
                t.setDescription(updatedTask.getDescription());
                t.setCompleted(updatedTask.isCompleted());
                return ResponseEntity.ok(taskService.save(t));
            })
            .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long id)
    {
        return taskService.findById(id)
            .filter(t -> t.getUser().getUsername().equals(userDetails.getUsername()))
            .map(t -> {
                taskService.deleteById(id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleCompleted(@PathVariable Long id, 
                                                @AuthenticationPrincipal UserDetails userDetails) 
    {
                                                
        Optional<Task> optionalTask = taskService.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
        if (!task.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        task.setCompleted(!task.isCompleted());
        Task updatedTask = taskService.save(task);

        return ResponseEntity.ok(updatedTask); 
            } else {
        return ResponseEntity.notFound().build();
        }
    }
}
