package com.stedikupujuci.stedisa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Product;
import com.stedikupujuci.stedisa.model.Subcategory;

@Service
public interface ProductService {

	Product saveProduct(Product product);

	List<Product> fetchProductList();

	List<Product> findByName(String name);

	List<Product> findBySubcategory(Subcategory subcategory);
	
	List<Product> findByExternalId(String externalId);

	Product updateProduct(Product product, Long idd);

	void deleteProductById(Long id);

}
