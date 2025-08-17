package com.Grocery.model;

import jakarta.persistence.*;

@Entity
public class cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "grocery_item_id")
    private grocery groceryItem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;

    public cart() {}

    public cart(Long id, int quantity, grocery groceryItem, user user) {
        this.id = id;
        this.quantity = quantity;
        this.groceryItem = groceryItem;
        this.user = user;
        this.subtotal = groceryItem.getPrice() * quantity;
    }

    // Getters and Setters

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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public grocery getGroceryItem() {
        return groceryItem;
    }

    public void setGroceryItem(grocery groceryItem) {
        this.groceryItem = groceryItem;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }
}
