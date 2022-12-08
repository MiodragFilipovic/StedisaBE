package com.stedikupujuci.stedisa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Category;

@Service
public interface CategoryService {

	Category saveCategory(Category category);

	List<Category> fetchCategoryList();
	
	List<Category> findByName(String name);
	
	List<Category> findByUrl(String url);

	Optional<Category> findById(Long id);
	
	Category updateCategory(Category category, Long idd);

	void deleteCategoryById(Long id);
	
	
}
