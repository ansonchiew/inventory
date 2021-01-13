package anson.inventory.validator;

import static anson.inventory.validator.InventoryValidator.BLANK_PROPERTIES;
import static anson.inventory.validator.InventoryValidator.NEGATIVE_QTY;
import static anson.inventory.validator.InventoryValidator.WRONG_CATEGORY;
import static anson.inventory.validator.InventoryValidator.SUB_CATEGORY_NOT_FOUND;
import static anson.inventory.validator.InventoryValidator.DUBPLICATE_INVENTORY;

import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import anson.inventory.Application;
import anson.inventory.config.TilesConfiguration;
import anson.inventory.entity.Category;
import anson.inventory.entity.Inventory;
import anson.inventory.entity.SubCategory;
import anson.inventory.entity.SubCategoryPk;
import anson.inventory.exception.ValidationException;
import anson.inventory.repository.InventoryRepository;
import anson.inventory.repository.SubCategoryRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={Application.class})
@WebAppConfiguration
public class InventoryValidatorTest {

	@Autowired
	private InventoryValidator inventoryValidator;
	
	@MockBean	
	private InventoryRepository inventoryRepo;

	@MockBean	
	private SubCategoryRepository subCategoryRepo;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void validateInventoryWithBlankCategoryName() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(BLANK_PROPERTIES);

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryValidator.validateBlankProperties(inventory);			
	}	

	@Test
	public void validateInventoryWithBlankSubCategoryName() throws ValidationException {
		String subCategoryName = "";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(BLANK_PROPERTIES);

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryValidator.validateBlankProperties(inventory);			
	}	
	
	@Test
	public void validateInventoryWithBlankInventoryName() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "";
		int quantity = 5;
	
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(BLANK_PROPERTIES);

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryValidator.validateBlankProperties(inventory);			
	}	
	
	@Test(expected = Test.None.class)
	public void validateInventoryWithoutBlankProperties() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;

		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryValidator.validateBlankProperties(inventory);			
	}	
	
	@Test
	public void validateSubCategoryWithNonExistingSubCategory() throws ValidationException {
		String subCategoryName = "Cake";
		
		String msg = String.format(SUB_CATEGORY_NOT_FOUND, subCategoryName);
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
		
		SubCategory subCategory = new SubCategory();
		subCategory.setName(subCategoryName);
		
		Inventory inventory = new Inventory();
		inventory.setSubCategory(subCategory);
		inventoryValidator.validateSubCategory(inventory, null);		
	}
		
	@Test
	public void validateSubCategoryWithWrongCategory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Clothing";
		String inventoryName = "Cup Cake";
		int quantity = 5;
				
		String msg = String.format(WRONG_CATEGORY, subCategoryName, categoryName); 
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
	
		String actualCategoryName = "Food";
		Category actualCategory = new Category(1, actualCategoryName);
		SubCategoryPk actualSubCategoryPk = new SubCategoryPk(1, actualCategory);
		SubCategory actualSubCategory = new SubCategory(actualSubCategoryPk, subCategoryName);
							
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryValidator.validateSubCategory(inventory,  actualSubCategory);	
	}
		
	@Test(expected = Test.None.class)
	public void validateSubCategoryWithCorrectCategory() throws ValidationException {
		String subCategoryName = "Cake";
		String categoryName = "Food";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		String actualCategoryName = categoryName;
		Category actualCategory = new Category(1, actualCategoryName);
		SubCategoryPk actualSubCategoryPk = new SubCategoryPk(1, actualCategory);
		SubCategory actualSubCategory = new SubCategory(actualSubCategoryPk, subCategoryName);
						
		Inventory inventory = createInventory(subCategoryName, categoryName, inventoryName, quantity);
		inventoryValidator.validateSubCategory(inventory,  actualSubCategory);	
	}
	
	@Test
	public void validateDuplicateInventoryWithExisitngInventory() throws ValidationException {
		String inventoryName = "Cake";
		Inventory inventory = new Inventory();
		inventory.setName(inventoryName);
		
		String msg = String.format(DUBPLICATE_INVENTORY, inventoryName); 
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(msg);
			
		when(inventoryRepo.findByName(inventoryName)).thenReturn(new Inventory());
		
		inventoryValidator.validateDuplicateInventory(inventory);		
	}
	
	@Test(expected = Test.None.class)
	public void validateDuplicateInventoryWithNonExistingInventory() throws ValidationException {
		String inventoryName = "Cup Cake";
		Inventory inventory = new Inventory();
		inventory.setName(inventoryName);
			
		when(inventoryRepo.findByName(inventoryName)).thenReturn(null);
		
		inventoryValidator.validateDuplicateInventory(inventory);		
	}
	
	@Test
	public void validateNegativeQuantity() throws ValidationException {
		String method = "validateQuantity";
		
		int quantity = -5;
		
		expectedException.expect(ValidationException.class);
		expectedException.expectMessage(NEGATIVE_QTY);
				
		inventoryValidator.validateQuantity(quantity);		
	}

	@Test(expected = Test.None.class)
	public void validateZeroQuantity() throws ValidationException {
		int quantity = 0;
		inventoryValidator.validateQuantity(quantity);		
	}

	@Test(expected = Test.None.class)
	public void validatePositiveQuantity() throws ValidationException {
		int quantity = 1;
		inventoryValidator.validateQuantity(quantity);		
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