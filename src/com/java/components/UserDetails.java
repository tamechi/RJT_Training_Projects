package com.java.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.NonNull;

import com.java.util.MobileNumberConstraint;

@Entity
@Table(name = "USER_TABLE")
@DynamicUpdate
public class UserDetails {

	public enum Gender {
		MALE, FEMALE
	}

	private long userId;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;
	private Gender gender;
	
	private long mobileNumber;
	
	private Set<Card> cards;
	private Set<Address> addresses;
	private Set<Order> orders;
	private Cart cart;
	
	@Override
	public String toString() {
		return "UserDetails [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", gender="
				+ gender + ", mobileNumber=" + mobileNumber + ", cards=" + cards + ", addresses=" + addresses
				+ ", orders=" + orders + ", cart=" + cart + "]";
	}

	private User user;

	public UserDetails() {
		super();
		this.cards = new HashSet<>();
		this.addresses = new HashSet<>();
		this.cart = new Cart();
	}

	@Id
	@GeneratedValue
	@Column(name = "USER_ID")
	public long getUserId() {
		return userId;
	}

	public void setUserId(long id) {
		this.userId = id;
	}

	@Column(name = "FIRST_NAME", nullable = false, length = 50)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME", nullable = false, length = 50)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "USER_GENDER", nullable = true, length = 20)
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Column(name = "MOBILE_NO", nullable = true)
	public long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@OneToOne(mappedBy="userDetails", cascade=CascadeType.ALL)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name = "USER_CARDS", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "CARD_ID") })
	public Set<Card> getCards() {
		return cards;
	}

	public void setCards(Set<Card> cards) {
		this.cards = cards;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name = "USER_ADDRESSES", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "ADDRESS_ID") })
	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name = "USER_ORDERS", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "ORDER_ID") })
	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	public void addOrders(List<Order> orders) {
		this.orders.addAll(orders);
	}

	public void addAddress(Address address) {
		this.addresses.add(address);
	}

	public void addCard(Card card) {
		this.cards.add(card);
	}

	public void addCartEntry(CartEntry entry) {
		this.cart.getCartEntries().add(entry);
	}
	
}
