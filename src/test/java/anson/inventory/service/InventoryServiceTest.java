package anson.inventory.service;

import static anson.inventory.validator.InventoryValidator.BLANK_PROPERTIES;
import static anson.inventory.validator.InventoryValidator.NEGATIVE_QTY;
import static anson.inventory.service.InventoryServiceImpl.INVENTORY_NOT_FOUND;
import static anson.inventory.service.InventoryServiceImpl.NULL_INVENTORY;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import anson.inventory.repository.InventoryRepository;
import anson.inventory.repository.SubCategoryRepository;
import anson.inventory.Application;
import anson.inventory.entity.Category;
import anson.inventory.entity.Inventory;
import anson.inventory.entity.SubCategory;
import anson.inventory.entity.SubCategoryPk;
import anson.inventory.exception.ValidationException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={Application.class})
@WebAppConfiguration
public class InventoryServiceTest {

	@Autowired
	private InventoryServiceImpl inventoryService;

	@MockBean	
	private InventoryRepository inventoryRepo;

	@MockBean	
	private SubCategoryRepository subCategoryRepo;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
		
	@Test
	public void createInventoryWithBlankCategoryName() throws Exception {
		String subCategoryName = "Cake";
		String categoryName = "";
		String inventoryName = "Cup Cake";
		int quantity = 5;

		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(BLANK_PROPERTIES);

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);	
	}	

	@Test
	public void createInventoryWithBlankSubCategoryName() throws Exception {
		String subCategoryName = "";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;

		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(BLANK_PROPERTIES);
	
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);		
	}	
	
	@Test
	public void createInventoryWithBlankInventoryName() throws Exception {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "";
		int quantity = 5;

		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(BLANK_PROPERTIES);
	
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);		
	}	
	
	@Test
	public void createInventoryWithNullInventory() throws ValidationException {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage(NULL_INVENTORY);
		
		inventoryService.createInventory(null);		
	}

	@Test
	public void createInventoryWithNonExistingSubCategory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		String msg = "Sub-Category " + subCategoryName + " is not found.";
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
		
		SubCategory subCategory = new SubCategory();
		subCategory.setName(subCategoryName);
				
		when(subCategoryRepo.findByName(subCategoryName)).thenReturn(null);
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);		
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
	
		String actualCategoryName = "Food";
		Category actualCategory = new Category(1, actualCategoryName);
		SubCategoryPk actualSubCategoryPk = new SubCategoryPk(1, actualCategory);
		SubCategory actualSubCategory = new SubCategory(actualSubCategoryPk, subCategoryName);
				
		when(subCategoryRepo.findByName(subCategoryName)).thenReturn(actualSubCategory);
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);				
	}	

	@Test
	public void createExistingInventory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";		
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		String actualCategoryName = categoryName;
		Category actualCategory = new Category(1, actualCategoryName);
		SubCategoryPk actualSubCategoryPk = new SubCategoryPk(1, actualCategory);
		SubCategory actualSubCategory = new SubCategory(actualSubCategoryPk, subCategoryName);
				
		String msg = "Inventory " + inventoryName + " is already exist."; 
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
				
		when(subCategoryRepo.findByName(subCategoryName)).thenReturn(actualSubCategory);
		when(inventoryRepo.findByName(inventoryName)).thenReturn(new Inventory());

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);		
	}
	
	@Test
	public void createInventoryWithNegativeQuantity() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = -5;

		String actualCategoryName = categoryName;
		Category actualCategory = new Category(1, actualCategoryName);
		SubCategoryPk actualSubCategoryPk = new SubCategoryPk(1, actualCategory);
		SubCategory actualSubCategory = new SubCategory(actualSubCategoryPk, subCategoryName);
	
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(NEGATIVE_QTY);
				
		when(subCategoryRepo.findByName(subCategoryName)).thenReturn(actualSubCategory);
		when(inventoryRepo.findByName(inventoryName)).thenReturn(null);

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);		
	}
	
	@Test
	public void createInventoryWithZeroQuantity() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 0;

		String actualCategoryName = categoryName;
		Category actualCategory = new Category(1, actualCategoryName);
		SubCategoryPk actualSubCategoryPk = new SubCategoryPk(1, actualCategory);
		SubCategory actualSubCategory = new SubCategory(actualSubCategoryPk, subCategoryName);
	
		when(subCategoryRepo.findByName(subCategoryName)).thenReturn(actualSubCategory);
		when(inventoryRepo.findByName(inventoryName)).thenReturn(null);

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);		
		verify(inventoryRepo, times(1)).save(inventory);		
	}

	@Test
	public void createInventoryWithPositiveQuantity() throws Exception {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		String actualCategoryName = categoryName;
		Category actualCategory = new Category(1, actualCategoryName);
		SubCategoryPk actualSubCategoryPk = new SubCategoryPk(1, actualCategory);
		SubCategory actualSubCategory = new SubCategory(actualSubCategoryPk, subCategoryName);

		when(subCategoryRepo.findByName(subCategoryName)).thenReturn(actualSubCategory);
		when(inventoryRepo.findByName(inventoryName)).thenReturn(null);

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryService.createInventory(inventory);		
		verify(inventoryRepo, times(1)).save(inventory);		
	}
	
	@Test
	public void updateExistingInventoryWithNegativeQuantity() throws ValidationException{
		int inventoryId = 1;
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		int newQuantity = -5;
		
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(NEGATIVE_QTY);
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventory.setId(inventoryId);
		when(inventoryRepo.findById(inventoryId)).thenReturn(Optional.of(inventory));
		
		inventory.setQuantity(newQuantity);
		inventoryService.updateInventory(inventory);		
	}
	
	@Test
	public void updateExistingInventoryWithPositiveQuantity() throws ValidationException{
		int inventoryId = 1;
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		int newQuantity = 10;
		
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventory.setId(inventoryId);
		when(inventoryRepo.findById(inventoryId)).thenReturn(Optional.of(inventory));
						
		inventory.setQuantity(newQuantity);
		inventoryService.updateInventory(inventory);		
		verify(inventoryRepo, times(1)).save(inventory);		
	}
	
	@Test
	public void updateNonExistingInventory() throws ValidationException{
		int inventoryId = 1;
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;
						
		when(inventoryRepo.findById(inventoryId)).thenReturn(Optional.empty());
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventory.setId(inventoryId);

		expectedException.expect(ResourceNotFoundException.class);
		expectedException.expectMessage(String.format(INVENTORY_NOT_FOUND, inventoryId));
		
		inventoryService.updateInventory(inventory);
	}
	
	@Test
	public void getExistingInventory() {
		int inventoryId = 1;
		Inventory inventory = new Inventory();
		inventory.setId(inventoryId);
		when(inventoryRepo.findById(inventoryId)).thenReturn(Optional.of(inventory));
		
		assertThat(inventoryService.getInventory(inventoryId), equalTo(inventory));		
	}
	
	@Test
	public void getNonExistingInventory() {
		int inventoryId = 1;
		when(inventoryRepo.findById(inventoryId)).thenReturn(Optional.empty());
		
		assertThat(inventoryService.getInventory(inventoryId), nullValue());		
	}
	
	@Test
	public void findAll() {
		String category = "Food";
		String subCategory = "Cake";
		String name1 = "Cup Cake";
		int quantity = 5;
		int inventory1Id = 1;
		
		String name2 = "Butter Cake";
		int inventory2Id = 2;
		
		Inventory inventory1 = createInventory(category, subCategory, name1, quantity);
		inventory1.setId(inventory1Id);
	
		Inventory inventory2 = createInventory(category, subCategory, name2, quantity);
		inventory1.setId(inventory2Id);
				
		List<Inventory> inventoryList = Arrays.asList(new Inventory[]{inventory1, inventory2});
	
		when(inventoryService.findAll()).thenReturn(inventoryList);
		
		// sorted by name
		assertThat(inventoryService.findAll(), contains(inventory2, inventory1));	
	}	
		
	@Test
	public void deleteExisintInventory() {
		int inventoryId = 1;
		Inventory inventory = new Inventory();
		inventory.setId(inventoryId);
	
		when(inventoryRepo.findById(inventoryId)).thenReturn(Optional.of(inventory));
		
		inventoryService.deleteInventory(inventoryId);
		verify(inventoryRepo, times(1)).delete(inventory);		
	}

	@Test
	public void deleteNonExistingInventory() throws ValidationException{
		int inventoryId = 1;
						
		when(inventoryRepo.findById(inventoryId)).thenReturn(Optional.empty());
	
		expectedException.expect(ResourceNotFoundException.class);
		expectedException.expectMessage(String.format(INVENTORY_NOT_FOUND, inventoryId));
		
		inventoryService.deleteInventory(inventoryId);
	}
	
	private Inventory createInventory(String subCategoryName, String categoryName, String inventoryName, int quantity) {
		Category category = new Category();
		category.setName(categoryName);
		SubCategoryPk subCategoryPk = new SubCategoryPk();
		subCategoryPk.setCategory(category);
		SubCategory subCategory = new SubCategory(subCategoryPk, subCategoryName);
		return new Inventory(subCategory, inventoryName, quantity);
	}
	
}