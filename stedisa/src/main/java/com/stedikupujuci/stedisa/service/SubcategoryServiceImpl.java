package com.stedikupujuci.stedisa.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Subcategory;
import com.stedikupujuci.stedisa.repository.SubcategoryRepository;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {
	
	@Autowired
    private SubcategoryRepository subcategoryRepository;

	@Override
	public List<Subcategory> fetchSubcategoryList() {
	    return (List<Subcategory>)
	            subcategoryRepository.findAll();
	}

	@Override
	public void deleteSubcategoryById(Long id) {
		subcategoryRepository.deleteById(id);
	}

	@Override
	public Subcategory saveSubcategory(Subcategory kateogrija) {
		 return subcategoryRepository.save(kateogrija);
	}

	@Override
	public Subcategory updateSubcategory(Subcategory subcategory, Long id) {
		Subcategory katDB
         = subcategoryRepository.findById(id)
               .get();

     if (Objects.nonNull(subcategory.getName())
         && !"".equalsIgnoreCase(
        		 subcategory.getName())) {
    	 katDB.setName(subcategory.getName());
     }

     if (Objects.nonNull(
    		 subcategory.getUrl())
         && !"".equalsIgnoreCase(
        		 subcategory.getUrl())) {
    	 katDB.setUrl(subcategory.getUrl());
     }


     return subcategoryRepository.save(katDB);
	}

	@Override
	public List<Subcategory> findByName(String name) {
		return subcategoryRepository.findByName(name);
	}

	@Override
	public List<Subcategory> findByUrl(String url) {
		return subcategoryRepository.findByUrl(url);
	}

}
