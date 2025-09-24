package com.Grocery.repository;

import com.Grocery.model.cart;
import com.Grocery.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface cartRepo extends JpaRepository<cart, Long> {

	// ✅ Add these 3 custom methods:

	List<cart> findByUserId(Long userId);

	void deleteByGroceryItemId(Long groceryItemId);

	void deleteByUserId(long id);

	cart findByGroceryItemIdAndUser(Long groceryId, user loggedInUser);
}
