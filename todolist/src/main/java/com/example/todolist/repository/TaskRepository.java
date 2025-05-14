package com.example.todolist.repository;

import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
   List<Task> findByUserId(Long userId);

}
