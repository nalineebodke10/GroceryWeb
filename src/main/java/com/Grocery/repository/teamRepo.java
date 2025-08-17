package com.Grocery.repository;

import com.Grocery.model.teamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface teamRepo extends JpaRepository<teamMember, Long> {
}
