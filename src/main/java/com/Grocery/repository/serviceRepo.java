package com.Grocery.repository;

import com.Grocery.model.service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface serviceRepo extends JpaRepository<service, Long> {
}
