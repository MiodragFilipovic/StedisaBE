package com.stedikupujuci.stedisa.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
    private CategoryRepository categoryRepository;

	@Override
	public List<Category> fetchCategoryList() {
	    return (List<Category>)
	            categoryRepository.findAll();
	}

	@Override
	public void deleteCategoryById(Long id) {
		categoryRepository.deleteById(id);
	}

	@Override
	public Category saveCategory(Category kateogrija) {
		 return categoryRepository.save(kateogrija);
	}

	@Override
	public Category updateCategory(Category category, Long id) {
		Category katDB
         = categoryRepository.findById(id)
               .get();

     if (Objects.nonNull(category.getName())
         && !"".equalsIgnoreCase(
        		 category.getName())) {
    	 katDB.setName(category.getName());
     }

     if (Objects.nonNull(
    		 category.getUrl())
         && !"".equalsIgnoreCase(
        		 category.getUrl())) {
    	 katDB.setUrl(category.getUrl());
     }


     return categoryRepository.save(katDB);
	}

	@Override
	public List<Category> findByName(String name) {
		return categoryRepository.findByName(name);
	}

	@Override
	public List<Category> findByUrl(String url) {
		return categoryRepository.findByUrl(url);
	}

}
