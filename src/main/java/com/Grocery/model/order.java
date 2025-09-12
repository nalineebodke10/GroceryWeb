package com.Grocery.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
public class order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String customerName;
    private String productName;
    private int quantity;
    private LocalDate date;
    private String status;
    @ManyToOne
    @JoinColumn(name = "user_id") // FK reference to user table
    private user user;
    
    private double totalAmount;  

    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<orderItem> orderItems;

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getDate() {
        return date;
    }
    
    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {    // ✅ added getter
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {   // ✅ added setter
        this.totalAmount = totalAmount;
    }

    public List<orderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<orderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
