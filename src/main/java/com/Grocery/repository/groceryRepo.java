package com.Grocery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Grocery.model.grocery;

public interface groceryRepo extends JpaRepository<grocery, Long> {
  

	// ✅ This will work for multiple groceries
	List<grocery> findByDeleteFalseAndCategoryIsDeleteFalse();


	   List<grocery> findByCategoryIdAndDeleteFalseAndCategoryIsDeleteFalse(Long categoryId);



	   List<grocery> findTop8ByDeleteFalseAndCategoryIsDeleteFalseOrderByDiscountPercentDesc();



}
