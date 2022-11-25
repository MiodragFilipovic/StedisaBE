package com.stedikupujuci.stedisa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Subcategory;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
	
	List<Subcategory> findByName(String name);
	
	List<Subcategory> findByUrl(String url);

}
