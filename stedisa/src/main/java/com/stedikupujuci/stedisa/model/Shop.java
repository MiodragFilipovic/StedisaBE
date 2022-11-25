package com.stedikupujuci.stedisa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Shop {

	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	private String logoURL;
	
	
}
