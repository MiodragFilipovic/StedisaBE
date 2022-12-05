package com.stedikupujuci.stedisa.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "price_history")
public class PriceHistory  {
	
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
    Long id;
	
	@ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    LocalDateTime date;

    double price;
    
	public PriceHistory() {
		super();
	}

	public PriceHistory(Long id, Product product, Shop shop, LocalDateTime date, double price) {
		super();
		this.id = id;
		this.product = product;
		this.shop = shop;
		this.date = date;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
