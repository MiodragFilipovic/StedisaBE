package com.stedikupujuci.stedisa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
	
	List<Shop> findByName(String name);
	

}
