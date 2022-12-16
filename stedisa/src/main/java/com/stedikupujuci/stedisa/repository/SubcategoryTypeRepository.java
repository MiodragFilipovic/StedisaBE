package com.stedikupujuci.stedisa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Subcategory;
import com.stedikupujuci.stedisa.model.SubcategoryType;

@Repository
public interface SubcategoryTypeRepository extends JpaRepository<SubcategoryType, Long> {
	
	Optional<SubcategoryType> findByName(String name);
	
	Optional<SubcategoryType> findById(Long id);
	
	List<SubcategoryType> findBySubcategory(Subcategory subcategory);

}
