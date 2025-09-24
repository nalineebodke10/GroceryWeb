package com.Grocery.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class user {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String userName;

	@Column(unique = true)
	private String mobile;

	private String address;

	private String password;

	private String pincode;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime registerAt;

	private String profilePhoto;

	// Constructors
	public user() {
	}

	public user(long id, String userName, String mobile, String address, String password, String pincode,
			LocalDateTime registerAt, String profileImage) {
		this.id = id;
		this.userName = userName;
		this.mobile = mobile;
		this.address = address;
		this.pincode = pincode;
		this.password = password;
		this.registerAt = registerAt;
		this.profilePhoto = profileImage;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	// Getters and Setters
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public LocalDateTime getRegisterAt() {
		return registerAt;
	}

	public void setRegisterAt(LocalDateTime registerAt) {
		this.registerAt = registerAt;
	}
}
