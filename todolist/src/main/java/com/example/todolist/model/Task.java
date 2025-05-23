package com.example.todolist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    // Getters y setters
    public Long getId()
    {
         return id; 
    }
    public void setId(Long id)
    {
        this.id = id; 
    }

    public String getTitle() 
    { 
        return title; 
    }
    public void setTitle(String title) 
    { 
        this.title = title; 
    }

    public String getDescription() 
    { 
        return description; 
    }
    public void setDescription(String description) 
    { 
        this.description = description; 
    }

    public boolean isCompleted() 
    { 
        return completed; 
    }
    public void setCompleted(boolean completed) 
    { 
        this.completed = completed; 
    }

    public User getUser() 
    { 
        return user; 
    }
    public void setUser(User user) 
    { 
        this.user = user; 
    }
}
