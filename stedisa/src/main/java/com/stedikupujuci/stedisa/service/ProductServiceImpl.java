package com.stedikupujuci.stedisa.service;

import java.util.List;
import java.util.Locale.Category;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Product;
import com.stedikupujuci.stedisa.model.Subcategory;
import com.stedikupujuci.stedisa.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<Product> fetchProductList() {
		return (List<Product>) productRepository.findAll();
	}

	@Override
	public void deleteProductById(Long id) {
		productRepository.deleteById(id);
	}

	@Override
	public Product saveProduct(Product kateogrija) {
		return productRepository.save(kateogrija);
	}

	@Override
	public Product updateProduct(Product product, Long id) {
		Product katDB = productRepository.findById(id).get();

		if (Objects.nonNull(product.getName()) && !"".equalsIgnoreCase(product.getName())) {
			katDB.setName(product.getName());
		}

		if (Objects.nonNull(product.getExternalId()) && !"".equalsIgnoreCase(product.getExternalId())) {
			katDB.setExternalId(product.getExternalId());
		}

		return productRepository.save(katDB);
	}

	@Override
	public List<Product> findByName(String name) {
		return productRepository.findByName(name);
	}

	@Override
	public List<Product> findByExternalId(String externalId) {
		return productRepository.findByExternalId(externalId);
	}

	@Override
	public List<Product> findBySubcategory(Subcategory subcategory) {
		return productRepository.findBySubcategory(subcategory);
	}

}
