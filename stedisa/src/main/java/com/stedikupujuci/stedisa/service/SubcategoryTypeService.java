package com.stedikupujuci.stedisa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Subcategory;
import com.stedikupujuci.stedisa.model.SubcategoryType;

@Service
public interface SubcategoryTypeService {

	SubcategoryType saveSubcategoryType(SubcategoryType subcategoryType);

	List<SubcategoryType> fetchSubcategoryTypeList();
	
	Optional<SubcategoryType> findByName(String name);
	
	Optional<SubcategoryType> findById(Long id);
	
	List<SubcategoryType> findBySubcategory(Subcategory subcategory);
	
	SubcategoryType updateSubcategoryType(SubcategoryType subcategory, Long id);

	void deleteSubcategoryTypeById(Long id);
	
	
}
