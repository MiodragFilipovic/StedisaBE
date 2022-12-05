package com.stedikupujuci.stedisa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.PriceHistory;
import com.stedikupujuci.stedisa.repository.PriceHistoryRepository;

@Service
public class PriceHistoryServiceImpl implements PriceHistoryService {

	@Autowired
	private PriceHistoryRepository priceHistoryRepository;

	@Override
	public PriceHistory savePriceHistory(PriceHistory priceHistory) {
		return priceHistoryRepository.save(priceHistory);
	}

	@Override
	public List<PriceHistory> fetchPriceHistoryList() {
		return (List<PriceHistory>) priceHistoryRepository.findAll();
	}

	@Override
	public PriceHistory updatePriceHistory(PriceHistory priceHistory, Long id) {
		PriceHistory phDB = priceHistoryRepository.findById(id).get();
		return priceHistoryRepository.save(phDB);
	}

	@Override
	public void deletePriceHistoryById(Long id) {
		priceHistoryRepository.deleteById(id);
	}

	@Override
	public List<PriceHistory> findByProductExternalId(String productExternalId) {
		return (List<PriceHistory>) priceHistoryRepository.findByProductExternalId(productExternalId);
	}

}
