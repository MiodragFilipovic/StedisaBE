package com.stedikupujuci.stedisa.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String externalId;

	private String name;
	
	private String imageURL;
	
	@ManyToOne
    @JoinColumn(name="subcategory", nullable=false)
	private Subcategory subcategory;
	
	@ManyToOne
    @JoinColumn(name="subcategoryType", nullable=false)
	private SubcategoryType subcategoryType;
	
	@OneToMany(mappedBy = "product")
    Set<PriceHistory> priceHistory;

	public Subcategory getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(Subcategory subcategory) {
		this.subcategory = subcategory;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public SubcategoryType getSubcategoryType() {
		return subcategoryType;
	}

	public void setSubcategoryType(SubcategoryType subcategoryType) {
		this.subcategoryType = subcategoryType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

}
