package com.Grocery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class orderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int quantity;

	@ManyToOne
	@JoinColumn(name = "grocery_item_id")
	private grocery groceryItem;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private order order;

	// --- Getters & Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public grocery getGroceryItem() {
		return groceryItem;
	}

	public void setGroceryItem(grocery groceryItem) {
		this.groceryItem = groceryItem;
	}

	public order getOrder() {
		return order;
	}

	public void setOrder(order order) {
		this.order = order;
	}

}
