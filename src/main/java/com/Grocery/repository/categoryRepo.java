package com.Grocery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Grocery.model.category;

@Repository
public interface categoryRepo extends JpaRepository<category, Long> {

	List<category> findByIsDeleteFalse();

}
