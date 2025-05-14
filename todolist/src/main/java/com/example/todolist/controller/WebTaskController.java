package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/tasks")
public class WebTaskController {

    private final TaskService taskService;
    private final UserService userService;

    public WebTaskController(TaskService taskService,
                             UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String listTasks(@AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        var user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        model.addAttribute("tasks", taskService.findByUserId(user.getId()));
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "tasks/form";
    }

    @PostMapping
    public String createTask(@AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute Task task) 
    {
        var user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        task.setUser(user);
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editTaskForm(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable Long id, Model model) 
        {
        var task = taskService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!task.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("task", task);
        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String updateTask(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Long id,
                             @ModelAttribute Task taskData) 
        {
        var task = taskService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!task.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        task.setTitle(taskData.getTitle());
        task.setDescription(taskData.getDescription());
        task.setCompleted(taskData.isCompleted());
        taskService.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Long id) 
    {
        var task = taskService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!task.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        taskService.deleteById(id);
        return "redirect:/tasks";
    }
}
