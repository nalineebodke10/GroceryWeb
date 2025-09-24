package com.Grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Grocery.model.admin;

public interface adminRepo extends JpaRepository<admin, Long> {
	admin findByUsernameAndPassword(String username, String password);
}
