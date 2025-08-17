package com.Grocery.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isDelete = false;

    private String name;

    private String image;

    private LocalDate createdDate;

    // Automatically set the createdDate when saving
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDate.now();
    }

    // Constructors
    public category() {
        this.isDelete = false;
    }

    public category(Long id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isDelete = false;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
}
