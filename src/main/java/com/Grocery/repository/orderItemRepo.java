package com.Grocery.repository;

import com.Grocery.model.orderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface orderItemRepo extends JpaRepository<orderItem, Long> {
}
