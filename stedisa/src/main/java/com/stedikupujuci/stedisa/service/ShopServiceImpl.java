package com.stedikupujuci.stedisa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.Shop;
import com.stedikupujuci.stedisa.repository.ShopRepository;

@Service
public class ShopServiceImpl implements ShopService {

	@Autowired
	ShopRepository shopRepository;

	@Override
	public Shop saveShop(Shop shop) {
		return shopRepository.save(shop);
	}

	@Override
	public List<Shop> fetchShopList() {
		return (List<Shop>) shopRepository.findAll();
	}

	@Override
	public List<Shop> findByName(String name) {
		return shopRepository.findByName(name);
	}

	@Override
	public Shop updateShop(Shop shop, Long id) {
		return shopRepository.save(shop);
	}

	@Override
	public void deleteShopById(Long id) {
		shopRepository.deleteById(id);

	}

}
