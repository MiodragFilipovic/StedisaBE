package com.stedikupujuci.stedisa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.PriceHistory;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

	List<PriceHistory> findByProductExternalId(String productExternalId);
}
