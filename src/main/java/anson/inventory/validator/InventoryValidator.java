package anson.inventory.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import anson.inventory.entity.Inventory;
import anson.inventory.entity.SubCategory;
import anson.inventory.exception.ValidationException;
import anson.inventory.repository.InventoryRepository;
import anson.inventory.repository.SubCategoryRepository;

@Component
public class InventoryValidator {
	
	Logger logger = LoggerFactory.getLogger(InventoryValidator.class);
	
	public static final String BLANK_PROPERTIES = "Category, sub-category and name cannot be blank.";
	public static final String NEGATIVE_QTY = "Inventory quantity cannot be negative.";
	public static final String WRONG_CATEGORY =  "Sub-category %s should not in category %s.";
	public static final String SUB_CATEGORY_NOT_FOUND = "Sub-Category %s is not found.";	
	public static final String DUBPLICATE_INVENTORY = "Inventory %s is already exist."; 
			
	@Autowired
    private InventoryRepository inventoryRepo;

	@Autowired
    private SubCategoryRepository subCategoryRepo;

	public InventoryRepository getInventoryRepo() {
		return inventoryRepo;
	}

	public void setInventoryRepo(InventoryRepository inventoryRepo) {
		this.inventoryRepo = inventoryRepo;
	}

	public SubCategoryRepository getSubCategoryRepo() {
		return subCategoryRepo;
	}

	public void setSubCategoryRepo(SubCategoryRepository subCategoryRepo) {
		this.subCategoryRepo = subCategoryRepo;
	}

	public void validate(Inventory inventory, SubCategory subCategory) throws ValidationException {
		validateBlankProperties(inventory);
		validateSubCategory(inventory, subCategory);
		validateDuplicateInventory(inventory);
		validateQuantity(inventory.getQuantity());
	}

	public void validateBlankProperties(Inventory inventory) throws ValidationException {
		String categoryName = inventory.getCategory().getName();
		String subCategoryName = inventory.getSubCategory().getName();
		String inventoryName = inventory.getName();
			
		if (StringUtils.isBlank(categoryName) 
			|| StringUtils.isBlank(subCategoryName)
			|| StringUtils.isBlank(inventoryName)) {
			String msg = BLANK_PROPERTIES;
			logger.error(msg);
			throw new ValidationException(msg);			
		}		
	}
	
	public void validateSubCategory(Inventory inventory, SubCategory subCategory) throws ValidationException {
		String subCategoryName = WordUtils.capitalizeFully(inventory.getSubCategory().getName());
				
		if (subCategory == null) {
			String msg = String.format(SUB_CATEGORY_NOT_FOUND, subCategoryName);
			logger.error(msg);
			throw new ValidationException(msg);			
		}
		
		String categoryName = WordUtils.capitalizeFully(inventory.getCategory().getName());

		if (!subCategory.getCategory().getName().equals(categoryName)) {
			String msg = String.format(WRONG_CATEGORY, subCategoryName, categoryName);
			logger.error(msg);
			throw new ValidationException(msg);			
		}
	}
	
	public void validateDuplicateInventory(Inventory inventory) throws ValidationException {
		String inventoryName = WordUtils.capitalizeFully(inventory.getName()); 
		inventory.setName(inventoryName);
		
		if (inventoryRepo.findByName(inventoryName) != null) {
			String msg = String.format(DUBPLICATE_INVENTORY, inventoryName); 
			logger.error(msg);
			throw new ValidationException(msg);			
		}			
	}
	
	public void validateQuantity(int quantity) throws ValidationException {
		if (quantity < 0) {
			String msg = NEGATIVE_QTY;
			logger.error(msg);
			throw new ValidationException(msg);		
		}
	}
	
}