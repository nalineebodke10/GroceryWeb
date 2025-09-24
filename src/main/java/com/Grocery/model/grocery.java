package com.Grocery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class grocery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private int price;
	private String description;
	private String image;

	@Column(name = "discount_percent", nullable = false)
	private Double discountPercent = 0.0;

	@Column(name = "discounted_price", nullable = false)
	private Double discountedPrice = 0.0;

	@Column(nullable = false)
	private Integer quantity = 1;

	@Column(name = "is_deleted")
	private Boolean delete = false;

	private LocalDateTime createdDate;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private category category;

	public grocery() {
	}

	@PrePersist
	@PreUpdate
	public void calculateDiscountedPrice() {
		if (discountPercent == null)
			discountPercent = 0.0;
		if (quantity == null)
			quantity = 1;
		if (delete == null)
			delete = false;
		if (createdDate == null)
			createdDate = LocalDateTime.now();

		this.discountedPrice = price - (price * discountPercent / 100.0);
	}

	// Getters and setters...

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Double getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(Double discountPercent) {
		this.discountPercent = discountPercent;
	}

	public Double getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(Double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Boolean isDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public category getCategory() {
		return category;
	}

	public void setCategory(category category) {
		this.category = category;
	}
}
