package com.stedikupujuci.stedisa.controller;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.PriceHistory;
import com.stedikupujuci.stedisa.model.Product;
import com.stedikupujuci.stedisa.model.Shop;
import com.stedikupujuci.stedisa.model.Subcategory;
import com.stedikupujuci.stedisa.service.CategoryService;
import com.stedikupujuci.stedisa.service.PriceHistoryService;
import com.stedikupujuci.stedisa.service.ProductService;
import com.stedikupujuci.stedisa.service.ShopService;
import com.stedikupujuci.stedisa.service.SubcategoryService;

@RestController
@RequestMapping(path = "/stedisa/rest/")
public class RESTController {

//	private final String apiURL = "https://cenoteka.rs";
//
//	private final String CATHEGORY_NAME_REGEX = "/kategorija/[a-zA-Z-]+";
//	private final String PRODUCT_REGEX = "data-product-id=\"[0-9]+";

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SubcategoryService subcategoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private PriceHistoryService priceHistoryService;

	@Autowired
	public RESTController() {

	}

	@GetMapping(path = "categories")
	@CrossOrigin(origins = "http://localhost:3000")
	public @ResponseBody ResponseEntity<Object> getCategories() {
		List<Category> categories = categoryService.fetchCategoryList();
		return ResponseEntity.status(HttpStatus.OK).body(categories);
	}

	@GetMapping(path = "categories/{id}")
	@CrossOrigin(origins = "http://localhost:3000")
	public @ResponseBody ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
		Optional<Category> categories = categoryService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(categories);
	}

	@GetMapping(path = "categories/{id}/subcategories")
	@CrossOrigin(origins = "http://localhost:3000")
	public @ResponseBody ResponseEntity<Object> getSubcategoriesByCategoryId(@PathVariable Long id) {
		List<Subcategory> subcategories = subcategoryService.findByCategory(categoryService.findById(id).get());
		return ResponseEntity.status(HttpStatus.OK).body(subcategories);
	}
	
	@GetMapping(path = "subcategories")
	@CrossOrigin(origins = "http://localhost:3000")
	public @ResponseBody ResponseEntity<Object> getSubcategories() {
		List<Subcategory> subcategories = subcategoryService.fetchSubcategoryList();
		return ResponseEntity.status(HttpStatus.OK).body(subcategories);
	}

	@GetMapping(path = "subcategories/{id}/products")
	@CrossOrigin(origins = "http://localhost:3000")
	public @ResponseBody ResponseEntity<Object> getProductsBySubcategory(@PathVariable Long id) {
		List<Product> productsBySubategory = productService.findBySubcategory(subcategoryService.findById(id).get());
		return ResponseEntity.status(HttpStatus.OK).body(productsBySubategory);
	}

}
