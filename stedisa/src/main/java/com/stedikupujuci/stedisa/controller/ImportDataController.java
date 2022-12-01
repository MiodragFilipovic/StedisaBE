package com.stedikupujuci.stedisa.controller;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
	private ShopService shopService;
	@Autowired
	private PriceHistoryService priceHistoryService;

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
				updateProduct(product);
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
		List<String> nonexistentProducts = new ArrayList<>();

		int productCount = 0;
		try {
			// iterate over subcategories
			for (Subcategory subcategory : subcategoryService.fetchSubcategoryList()) {
				// if subcategory is "on sale" continue
				if (subcategory.getUrl().contains("akcija")) {
					continue;
				}
				System.out.println(subcategory.getName());
				for (int i = 1; i < 10000; i++) {
					Document doc = Jsoup.connect(apiURL + subcategory.getUrl() + "?page=" + i).get();
					Elements articles = doc.select("div.article-row");
					if (articles.size() < 3) {
						break;
					}
					System.out.println();
					System.out.print(subcategory.getUrl() + "?page=" + i);
					int ordinalNumber = 1;
					for (Element article : articles) {
						String articleExternalId = article.attr("data-product-id");
						if (articleExternalId.equals("") || articleExternalId.equals("$ID")
								|| !article.attr("data-group-parent").equals(""))
							continue;

						Product product = null;
						try {
							product = productService.findByExternalId(article.attr("data-product-id")).get(0);
						} catch (Exception e1) {
							nonexistentProducts.add(articleExternalId);
							product = new Product();
							product.setExternalId(articleExternalId);
							product = insertNewProduct(product);
						}
						productCount++;
						System.out.println();
						System.out.println(String.format("Naziv proizvoda: %-80s [Rb: %3s Page: %3s]", product.getName(),
								ordinalNumber, i));
						;
						Elements articlePrices = article.getElementsByClass("article-price");
						for (Element articlePriceByShop : articlePrices) {
							try {
								Elements prices = articlePriceByShop.getElementsByClass("price");
								Elements shops = articlePriceByShop.getElementsByClass("shop");
								if (!prices.isEmpty() && !shops.isEmpty()) {
									Shop shop = shopService
											.findByName(shops.get(0).getElementsByTag("img").get(0).attr("alt")).get(0);
									PriceHistory priceHistory = new PriceHistory();
									priceHistory.setDate(LocalDateTime.now());
									priceHistory.setProduct(product);
									priceHistory.setShop(shop);
									NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
									String priceString = prices.get(0).ownText();
									if (priceString.equals("-")) {
										priceHistory.setPrice(0);
									} else {
										Number number = format.parse(priceString);
										priceHistory.setPrice(number.doubleValue());
									}
									priceHistoryService.savePriceHistory(priceHistory);
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						ordinalNumber++;
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		stopwatch.stop();
		System.out.println("ZAVRSEN UPDATE CENA");
		System.out.println("Ukupno prozivoda: " + productCount);
		System.out.println("Ukupno trajanje: ");
		System.out.println(stopwatch.getTotalTimeSeconds() + " sekundi");

		System.out.println("Proizvodi kojih nema u bazi proizvoda");
		for (String nonExistentProduct : nonexistentProducts) {
			System.out.println(nonExistentProduct);
		}

		return ResponseEntity.status(HttpStatus.OK).body("");
	}

	private void updateProduct(Product product) {
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

	private Product insertNewProduct(Product product) {
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
		return productService.saveProduct(product);
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
