package com.stedikupujuci.stedisa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stedikupujuci.stedisa.model.PriceHistory;

@Service
public interface PriceHistoryService {
	
	PriceHistory savePriceHistory(PriceHistory priceHistory);

	List<PriceHistory> fetchPriceHistoryList();

	PriceHistory updatePriceHistory(PriceHistory priceHistory, Long id);

	void deletePriceHistoryById(Long id);
	
	List<PriceHistory> findByProductExternalId(String productExternalId);

}
