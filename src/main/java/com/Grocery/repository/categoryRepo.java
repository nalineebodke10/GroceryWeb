package com.Grocery.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Grocery.model.category;

@Repository
public interface categoryRepo extends JpaRepository<category, Long> {

	// All active categories
	List<category> findByIsDeleteFalse();

	// Search by category name
	List<category> findByIsDeleteFalseAndNameContainingIgnoreCase(String name);

	// changes

	// Sort by name ASC/DESC
	List<category> findAllByOrderByNameAsc();

	List<category> findAllByOrderByNameDesc();

	// Filter by exact date
	List<category> findByCreatedDate(LocalDate date);

	// Filter by date range
	List<category> findByCreatedDateBetween(LocalDate start, LocalDate end);
}
