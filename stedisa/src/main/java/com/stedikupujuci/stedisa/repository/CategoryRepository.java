package com.stedikupujuci.stedisa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stedikupujuci.stedisa.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	List<Category> findByName(String name);
	
	List<Category> findByUrl(String url);
	
	Optional<Category> findById(Long id);

}
