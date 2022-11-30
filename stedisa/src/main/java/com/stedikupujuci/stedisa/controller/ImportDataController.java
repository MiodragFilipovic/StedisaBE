package com.stedikupujuci.stedisa.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.stedikupujuci.stedisa.model.Category;
import com.stedikupujuci.stedisa.model.Product;
import com.stedikupujuci.stedisa.model.Subcategory;
import com.stedikupujuci.stedisa.service.CategoryService;
import com.stedikupujuci.stedisa.service.ProductService;
import com.stedikupujuci.stedisa.service.SubcategoryService;

@RestController
@RequestMapping(path = "/stedisa/import/")
public class ImportDataController {

	private final String apiURL = "https://cenoteka.rs";

	private final String CATHEGORY_NAME_REGEX = "/kategorija/[a-zA-Z-]+";
	private final String PRODUCT_REGEX = "data-product-id=\"[0-9]+";

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SubcategoryService subcategoryService;
	@Autowired
	private ProductService productService;

	@Autowired
	public ImportDataController() {

	}

	@GetMapping(path = "categories")
	public @ResponseBody ResponseEntity<Object> importCategoriesFromWebsite() {
		List<Category> uniqueCategories = getUniqueCategories();
		StringBuilder responseBody = new StringBuilder();
		for (Category category : uniqueCategories) {
			if (categoryService.findByUrl(category.getUrl()).isEmpty()) {
				categoryService.saveCategory(category);
			}
			responseBody.append(category + "\n");
		}
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}

	private List<Category> getUniqueCategories() {
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(apiURL, String.class);

		Pattern pat = Pattern.compile(CATHEGORY_NAME_REGEX);
		Matcher mat = pat.matcher(response);
		List<Category> uniqueCategories = new ArrayList<>();
		while (mat.find()) {
			Category category = new Category();
			String url = mat.group();
			category.setName(createNameFromURL(url));
			category.setUrl(url);
			if (!uniqueCategories.contains(category)) {
				uniqueCategories.add(category);
			}
		}
		return uniqueCategories;
	}

	@GetMapping(path = "subcategories")
	public @ResponseBody ResponseEntity<Object> importSubcategoriesFromWebsite() {

		List<Subcategory> uniqueSubcategories = getUniqueSubcategories();
		for (Subcategory subcategory : uniqueSubcategories) {
			if (subcategoryService.findByUrl(subcategory.getUrl()).isEmpty()) {
				subcategoryService.saveSubcategory(subcategory);
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("");
	}

	private List<Subcategory> getUniqueSubcategories() {
		List<Subcategory> uniqueSubcategories = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		List<Category> cetegories = categoryService.fetchCategoryList();

		for (Category category : cetegories) {
			String response = restTemplate.getForObject(apiURL + category.getUrl(), String.class);
			Pattern pat = Pattern.compile(
					category.getUrl().replace("kategorija", "proizvodi").replace("akcije", "akcija") + "/[a-zA-Z-]+");
			Matcher mat = pat.matcher(response);

			while (mat.find()) {
				Subcategory subcategory = new Subcategory();
				subcategory.setCategory(category);

				String url = mat.group();
				subcategory.setName(createNameFromURL(url));
				subcategory.setUrl(url);
				if (!uniqueSubcategories.contains(subcategory)) {
					uniqueSubcategories.add(subcategory);
				}
				System.out.println(mat.group());
			}
		}
		return uniqueSubcategories;
	}

	@GetMapping(path = "products")
	public @ResponseBody ResponseEntity<Object> importProductsFromWebsite() {

		List<String> products = getUniqueProducts();

		for (String productId : products) {
			if (productService.findByExternalId(productId).isEmpty()) {
				Product product = new Product();
				product.setExternalId(productId);
				insertProduct(product);
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("");
	}

	private List<String> getUniqueProducts() {
		List<Subcategory> subcetegories = subcategoryService.fetchSubcategoryList();
		List<String> productsIDs = new ArrayList<>();
		for (Subcategory subcategory : subcetegories) {
			// if subcategory is "on sale" continue
			if (subcategory.getUrl().contains("akcija")) {
				continue;
			}
			int page = 1;
			List<String> productsIDsForCategory = new ArrayList<>();
			for (int i = 0; i < 10000; i++) {
				List<String> idsPerPage = getProductIdsFromSubategoryAndPage(subcategory, page);
				if (idsPerPage.size() > 1) {
					productsIDsForCategory.addAll(idsPerPage);
					page++;
				} else {
					productsIDs.addAll(productsIDsForCategory);
					break;
				}
			}

		}
		return productsIDs;
	}

	@GetMapping(path = "updatePrices")
	public @ResponseBody ResponseEntity<Object> updatePrices() {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		int productCount = 0;
		try {
			for (Subcategory subcategory : subcategoryService.fetchSubcategoryList()) {
				// if subcategory is "on sale" continue
				if (subcategory.getUrl().contains("akcija")) {
					continue;
				}
				System.out.println(subcategory.getName());
				int page = 1;
				for (int i = 1; i < 10000; i++) {
					System.out.println(apiURL + subcategory.getUrl() + "?page=" + i);
					Document doc = Jsoup.connect(apiURL + subcategory.getUrl() + "?page=" + i).get();
					Elements articles = doc.select("div.article-row");
					if (articles.size() < 3) {
						break;
					}
					int ordinalNumber = 1;
					for (Element article : articles) {
						if (article.attr("data-product-id").equals("") || article.attr("data-product-id").equals("$ID")
								|| !article.attr("data-group-parent").equals(""))
							continue;
						productCount++;
						Element articleName = article.getElementsByClass("article-name").get(0);
						System.out.println();
						System.out.print(articleName.text() + " ");
						System.out.println("RB: " + ordinalNumber + " Page: " + i);
						Elements articlePrices = article.getElementsByClass("article-price");
						for (Element element : articlePrices) {
							try {
								Elements prices = element.getElementsByClass("price");
								if (!prices.isEmpty()) {

									System.out.print(" (" + prices.get(0).text());
								}
								Elements shops = element.getElementsByClass("shop");
								if (!shops.isEmpty()) {
									System.out.print(
											" " + shops.get(0).getElementsByTag("img").get(0).attr("alt") + ") ");

								}
							} catch (Exception e) {
								continue;
							}
						}
						ordinalNumber++;
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("ZAVRSEN UPDATE CENA");
		System.out.println("Ukupno prozivoda: "+ productCount);
		stopwatch.stop(); // optional
		System.out.println(stopwatch.getTotalTimeSeconds());
		return ResponseEntity.status(HttpStatus.OK).body("");
	}

	private void insertProduct(Product product) {
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(apiURL + "/artikal/" + product.getExternalId(), String.class);

		product.setExternalId(String.valueOf(product.getExternalId()));
		product.setName(getProductNameFromResponseString(response));
		product.setImageURL(getProducImageURLFromResposnseString(response, product));

		Subcategory subcategory;
		try {
			subcategory = subcategoryService.findByName(getProductCategoryNameFromResponseString(response)).get(0);
			product.setSubcategory(subcategory);
		} catch (Exception e) {
			System.out.println("greska");
			e.printStackTrace();
		}
		productService.updateProduct(product, product.getId());
	}

	private String getProductNameFromResponseString(String response) {
		Pattern paternName = Pattern.compile("<title>[\\s\\S]+- Cenoteka<\\/title>");
		Matcher matName = paternName.matcher(response);
		while (matName.find()) {
			return matName.group().replace("<title>", "").replace("</title>", "").replace(" - Cenoteka", "");
		}
		return "";
	}

	private String getProducImageURLFromResposnseString(String response, Product product) {
		// preparing regex string
		String paternImageURLWithProductName = product.getName();
		paternImageURLWithProductName = paternImageURLWithProductName.replaceAll("\\(", "\\\\(");
		paternImageURLWithProductName = paternImageURLWithProductName.replaceAll("\\)", "\\\\)");
		paternImageURLWithProductName = paternImageURLWithProductName.replaceAll("\\.", "\\\\.");
		paternImageURLWithProductName = paternImageURLWithProductName.replaceAll("\\+", "\\\\+");
		paternImageURLWithProductName = paternImageURLWithProductName.replaceAll("\\%", "\\\\%");
		paternImageURLWithProductName = paternImageURLWithProductName.replaceAll("\\,", "\\\\,");
		paternImageURLWithProductName = paternImageURLWithProductName.replaceAll("\\*", "\\\\*");
		Pattern paternImageURL = Pattern
				.compile("<img src=\"\\/assets\\/images\\/articles[\\s\\S]+alt=\"" + paternImageURLWithProductName);
		Matcher matImageURL = paternImageURL.matcher(response);
		while (matImageURL.find()) {
			return matImageURL.group().split("\"")[1];
		}
		return "";
	}

	private String getProductCategoryNameFromResponseString(String response) {
		Pattern paternName = Pattern.compile("subcategory: '[\\s\\S]+'\\}\\)\\;");
		Matcher matName = paternName.matcher(response);
		String categoryName = "";
		while (matName.find()) {
			categoryName = matName.group().split("'")[1];
		}
		return categoryName;
	}

	private List<String> getProductIdsFromSubategoryAndPage(Subcategory subcategory, int page) {
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(apiURL + subcategory.getUrl() + "?page=" + String.valueOf(page),
				String.class);
		Pattern pat = Pattern.compile(PRODUCT_REGEX);
		Matcher mat = pat.matcher(response);
		List<String> idjeviproizvoda = new ArrayList<>();
		while (mat.find()) {
			idjeviproizvoda.add(mat.group().replace("data-product-id=\"", ""));
		}
		return idjeviproizvoda;
	}

	private String createNameFromURL(String url) {
		String nameFromURL = url.split("/")[3];
		String nameWithoutDashes = nameFromURL.replaceAll("-", " ");
		String capitalizedName = StringUtils.capitalize(nameWithoutDashes);
		return capitalizedName;
	}

}
