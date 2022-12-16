package com.stedikupujuci.stedisa.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Subcategory;
import com.stedikupujuci.stedisa.model.SubcategoryType;
import com.stedikupujuci.stedisa.repository.SubcategoryRepository;
import com.stedikupujuci.stedisa.repository.SubcategoryTypeRepository;

@Service
public class SubcategoryTypeServiceImpl implements SubcategoryTypeService {

	@Autowired
	private SubcategoryTypeRepository subcategoryTypeRepository;

	@Override
	public List<SubcategoryType> fetchSubcategoryTypeList() {
		return (List<SubcategoryType>) subcategoryTypeRepository.findAll();
	}

	@Override
	public void deleteSubcategoryTypeById(Long id) {
		subcategoryTypeRepository.deleteById(id);
	}

	@Override
	public SubcategoryType saveSubcategoryType(SubcategoryType subcategoryType) {
		return subcategoryTypeRepository.save(subcategoryType);
	}

	@Override
	public SubcategoryType updateSubcategoryType(SubcategoryType subcategoryType, Long id) {
		SubcategoryType subTypeDB = subcategoryTypeRepository.findById(id).get();

		if (Objects.nonNull(subcategoryType.getName()) && !"".equalsIgnoreCase(subcategoryType.getName())) {
			subTypeDB.setName(subcategoryType.getName());
		}

		return subcategoryTypeRepository.save(subTypeDB);
	}

	@Override
	public Optional<SubcategoryType> findByName(String name) {
		return subcategoryTypeRepository.findByName(name);
	}

	@Override
	public Optional<SubcategoryType> findById(Long id) {
		return subcategoryTypeRepository.findById(id);
	}

	@Override
	public List<SubcategoryType> findBySubcategory(Subcategory subcategory) {
		return subcategoryTypeRepository.findBySubcategory(subcategory);
	}

}
