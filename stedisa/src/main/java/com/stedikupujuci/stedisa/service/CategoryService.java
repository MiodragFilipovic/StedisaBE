package com.stedikupujuci.stedisa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Category;

@Service
public interface CategoryService {

	Category saveCategory(Category category);

	List<Category> fetchCategoryList();
	
	List<Category> findByName(String name);
	
	List<Category> findByUrl(String url);

	Category updateCategory(Category category, Long idd);

	void deleteCategoryById(Long id);
	
	
}
