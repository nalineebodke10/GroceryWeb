package com.Grocery.repository;

import com.Grocery.model.order;
import com.Grocery.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface orderRepo extends JpaRepository<order, Long> {

	// ✅ Get all orders by status (e.g., "Pending", "Confirmed")
	List<order> findByStatus(String status);

	// ✅ Get all orders placed by a specific user's mobile number
	List<order> findByUserId(user user);

	List<order> findByUser(user user);
}
