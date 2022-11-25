package com.stedikupujuci.stedisa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Subcategory;

@Service
public interface SubcategoryService {

	Subcategory saveSubcategory(Subcategory subcategory);

	List<Subcategory> fetchSubcategoryList();
	
	List<Subcategory> findByName(String name);
	
	List<Subcategory> findByUrl(String url);

	Subcategory updateSubcategory(Subcategory category, Long idd);

	void deleteSubcategoryById(Long id);
	
	
}
