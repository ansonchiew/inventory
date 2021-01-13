package anson.inventory.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import anson.inventory.entity.Category;
import anson.inventory.entity.Inventory;
import anson.inventory.entity.SubCategory;
import anson.inventory.entity.SubCategoryPk;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InventoryRepositoryTest {

	@Autowired
    private InventoryRepository inventoryRepo;

	@Autowired
    private CategoryRepository categoryRepo;

	@Autowired
    private SubCategoryRepository subCategoryRepo;
	
	@Test
	public void saveInventorySuccessfully() {
		
		String subCategoryName = "Cake";
		
		SubCategory subCategory = subCategoryRepo.findByName(subCategoryName);
		
		if (subCategory != null) {
			
			Inventory inventory = new Inventory(subCategory, "Cup Cake", 5);
			Inventory savedInventory = inventoryRepo.save(inventory);
			inventoryRepo.flush();
			
			assertThat(savedInventory.getName(), equalTo(inventory.getName()));		
			assertThat(savedInventory.getId(), notNullValue());;				
		} else {
			fail("SubCategory " + subCategoryName + " is not found");
		}
	}
	
	@Test(expected = DataIntegrityViolationException.class)	
	public void saveDuplicateInventory() {
		String subCategoryName = "Cake";
		String inventoryName = "Cup cake";
		
		SubCategory subCategory = subCategoryRepo.findByName(subCategoryName);
		
		if (subCategory != null) {
			
			Inventory inventory = new Inventory(subCategory, inventoryName, 5);
			inventoryRepo.save(inventory);		
		
			Inventory inventory2 = new Inventory(subCategory, inventoryName, 10);	
			inventoryRepo.save(inventory2);
			inventoryRepo.flush();			
		} else {
			fail("SubCategory " + subCategoryName + " is not found");
		}	
	}
	
	@Test
	public void updateQuantity() {
		String subCategoryName = "Cake";
		String inventoryName = "Cup cake";
		
		int quantity = 5;
		int newQuantity = 10;
		
		SubCategory subCategory = subCategoryRepo.findByName(subCategoryName);
		
		if (subCategory != null) {
			
			Inventory inventory = new Inventory(subCategory, inventoryName, quantity);
			inventory = inventoryRepo.save(inventory);		
			inventoryRepo.flush();			
		
			Optional<Inventory> inventoryOptional = inventoryRepo.findById(inventory.getId());
			
			Inventory savedInventory = inventoryOptional.get();
			savedInventory.setQuantity(newQuantity);
			inventory = inventoryRepo.save(inventory);		
			inventoryRepo.flush();			
		
			inventoryOptional = inventoryRepo.findById(inventory.getId());
			savedInventory = inventoryOptional.get();
			assertThat(savedInventory.getQuantity(), equalTo(newQuantity));
			
		} else {
			fail("SubCategory " + subCategoryName + " is not found");
		}		
	}
	
	@Test(expected = DataIntegrityViolationException.class)	
	public void saveInventoryWithWrongCategory() {
		String subCategoryName = "Cake";
		String categoryName = "Clothes";
		String inventoryName = "Cup cake";
		
		SubCategory subCategory = subCategoryRepo.findByName(subCategoryName);
		Category category = categoryRepo.findByName(categoryName);
		
		if (subCategory != null && category != null) {
			SubCategoryPk subCategoryPk = subCategory.getSubCategoryPk();
			subCategoryPk.setCategory(category);
			
			Inventory inventory = new Inventory(subCategory, inventoryName, 5);
			inventory = inventoryRepo.save(inventory);		
			inventoryRepo.flush();			
		} else {
			if (subCategory == null) {
				fail("SubCategory " + subCategoryName + " is not found");
			} else {
				fail("Category " + categoryName + " is not found");
			}
		}	
	}
	
	@Test
	public void findByName() {
		String subCategoryName = "Cake";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		SubCategory subCategory = subCategoryRepo.findByName(subCategoryName);
		
		if (subCategory != null) {			
			Inventory inventory = new Inventory(subCategory, inventoryName, quantity);
			inventory = inventoryRepo.save(inventory);
			
			assertThat(inventoryRepo.findByName(inventoryName), equalTo(inventory));			
		} else {
			fail("SubCategory " + subCategoryName + " is not found");
		}	
	}	
	
	@Test
	public void findAll() {
		String subCategoryName = "Cake";
		String inventoryName = "Cup Cake";
		int quantity = 5;
		
		SubCategory subCategory = subCategoryRepo.findByName(subCategoryName);
				
		if (subCategory != null) {			
			Inventory inventory = new Inventory(subCategory, inventoryName, quantity);
			inventory = inventoryRepo.save(inventory);
						
			assertThat(inventoryRepo.findAll().get(0), equalTo(inventory));			
		} else {
			fail("SubCategory " + subCategoryName + " is not found");
		}
	}
	
}