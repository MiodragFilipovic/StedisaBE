package com.stedikupujuci.stedisa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Shop;

@Service
public interface ShopService {

	Shop saveShop(Shop shop);

	List<Shop> fetchShopList();
	
	List<Shop> findByName(String name);
	
	Shop updateShop(Shop shop, Long idd);

	void deleteShopById(Long id);
	
	
}
