package com.stedikupujuci.stedisa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Product;
import com.stedikupujuci.stedisa.model.Subcategory;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	List<Product> findByName(String name);
	
	List<Product> findByExternalId(String externalId);
	
	List<Product> findBySubcategory(Subcategory subcategory); ;

}
