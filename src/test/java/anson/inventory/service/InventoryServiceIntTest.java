package anson.inventory.service;

import static anson.inventory.validator.InventoryValidator.NEGATIVE_QTY;
import static anson.inventory.service.InventoryServiceImpl.NULL_INVENTORY;
import static anson.inventory.service.InventoryServiceImpl.INVENTORY_NOT_FOUND;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import anson.inventory.config.TilesConfiguration;
import anson.inventory.entity.Category;
import anson.inventory.entity.Inventory;
import anson.inventory.entity.SubCategory;
import anson.inventory.entity.SubCategoryPk;
import anson.inventory.exception.ValidationException;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = "anson.inventory",  
			   excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TilesConfiguration.class}))
@DataJpaTest
@WebAppConfiguration
public class InventoryServiceIntTest {

	@Autowired
	private InventoryService inventoryService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
		
	@Test
	public void createInventoryWithNullInventory() throws ValidationException {		
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage(NULL_INVENTORY);
		
		inventoryService.createInventory(null);		
	}

	@Test
	public void createInventoryWithNonExistingSubCategory() throws ValidationException {
		String subCategoryName = "Trouser";
		String categoryName = "Clothes";		
		String inventoryName = "Jean";
		int quantity = 5;
			
		String msg = "Sub-Category " + subCategoryName + " is not found.";
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
		
		createInventory(subCategoryName, categoryName, inventoryName, quantity);
	}
	
	@Test
	public void createInventoryWithWrongCategory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Clothes";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		String msg = "Sub-category " + subCategoryName + " should not in category " + categoryName + "."; 
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
		
		createInventory(subCategoryName, categoryName, inventoryName, quantity);
	}	
	
	@Test
	public void createExistingInventory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);

		String msg = "Inventory " + inventoryName + " is already exist."; 
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
		
		inventoryService.createInventory(inventory);		
	}

	@Test
	public void createInventoryWithNegativeQuantity() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = -5;
		
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(NEGATIVE_QTY);

		createInventory(subCategoryName, categoryName, inventoryName, quantity);
	}
	
	@Test
	public void createInventoryWithZeroQuantity() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 0;
		
		assertThat(createInventory(subCategoryName, categoryName, inventoryName, quantity).getId(), notNullValue());	
	}

	@Test
	public void createInventoryWithPositiveQuantity() throws Exception {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;

		assertThat(createInventory(subCategoryName, categoryName, inventoryName, quantity).getId(), notNullValue());	
	}
	
	@Test
	public void updateExistingInventoryWithNegativeQuantity() throws ValidationException{
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		int newQuantity = -5;
					
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(NEGATIVE_QTY);
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventory.setQuantity(newQuantity);		
		inventoryService.updateInventory(inventory);		
	}

	@Test
	public void updateExistingInventoryWithPositiveQuantity() throws ValidationException{
		String subCategoryName = "Cake";
		String categoryName = "Food";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		int newQuantity = 10;
		inventory.setQuantity(newQuantity);		
		inventoryService.updateInventory(inventory);		

		assertThat(inventoryService.updateInventory(inventory).getQuantity(), equalTo(newQuantity));
	}
		
	@Test
	public void updateNonExistingInventory() throws ValidationException{
		int inventoryId = 1;
		String subCategoryName = "Cake";
		String categoryName = "Food";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity, false);
		inventory.setId(inventoryId);
		
		expectedException.expect(ResourceNotFoundException.class);
		expectedException.expectMessage(String.format(INVENTORY_NOT_FOUND, inventoryId));
		
		inventoryService.updateInventory(inventory);
	}
	
	@Test
	public void getExistingInventory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		int inventoryId = inventory.getId();
		
		assertThat(inventoryService.getInventory(inventoryId), equalTo(inventory));
	}
	
	@Test
	public void getNonExistingInventory() {
		int inventoryId = 1000;
		assertThat(inventoryService.getInventory(inventoryId), nullValue());
	}
		
	@Test
	public void getAll() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		createInventory(subCategoryName, categoryName, inventoryName, quantity);
		assertThat(inventoryService.findAll().size(), equalTo(1));		
	}
	
	@Test
	public void deleteExistingInventory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		int inventoryId = inventory.getId();
		
		inventoryService.deleteInventory(inventoryId);
		assertThat(inventoryService.getInventory(inventoryId), nullValue());		
	}
	
	@Test
	public void deleteNonExistingInventory() {
		int inventoryId = 1;
		
		expectedException.expect(ResourceNotFoundException.class);
		expectedException.expectMessage(String.format(INVENTORY_NOT_FOUND, inventoryId));
		
		inventoryService.deleteInventory(inventoryId);
	}	
	
	private Inventory createInventory(String subCategoryName, String categoryName, String inventoryName, int quantity) throws ValidationException {
		return createInventory(subCategoryName, categoryName, inventoryName, quantity, true);
	}
	
	private Inventory createInventory(String subCategoryName, String categoryName, String inventoryName, int quantity, boolean persist) throws ValidationException {
		Category category = new Category();
		category.setName(categoryName);
		SubCategoryPk subCategoryPk = new SubCategoryPk();
		subCategoryPk.setCategory(category);
		SubCategory subCategory = new SubCategory(subCategoryPk, subCategoryName);
		Inventory inventory = new Inventory(subCategory, inventoryName, quantity);
		if (persist) {
			return inventoryService.createInventory(inventory);	
		}
		return inventory;
	}	
			
}