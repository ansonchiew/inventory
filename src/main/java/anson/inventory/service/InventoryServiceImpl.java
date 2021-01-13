package anson.inventory.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import anson.inventory.entity.Inventory;
import anson.inventory.entity.SubCategory;
import anson.inventory.exception.ValidationException;
import anson.inventory.repository.InventoryRepository;
import anson.inventory.repository.SubCategoryRepository;
import anson.inventory.validator.InventoryValidator;

@Service
public class InventoryServiceImpl implements InventoryService {

	Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

	public static final String NULL_INVENTORY = "Inventory cannot be null.";
	public static final String INVENTORY_NOT_FOUND = "Inventory not found for ID: %d";
	
	@Autowired
    private InventoryRepository inventoryRepo;

	@Autowired
    private SubCategoryRepository subCategoryRepo;
	
	@Autowired
	private InventoryValidator inventoryValidator; 
	
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

	public InventoryValidator getInventoryValidator() {
		return inventoryValidator;
	}

	public void setInventoryValidator(InventoryValidator inventoryValidator) {
		this.inventoryValidator = inventoryValidator;
	}

	@Override
	public Inventory createInventory(Inventory inventory) throws ValidationException {
		logger.info("create inventory: " + inventory);		
		if (inventory == null) {
			throw new NullPointerException(NULL_INVENTORY);
		}
		String subCategoryName = WordUtils.capitalizeFully(inventory.getSubCategory().getName());
		SubCategory subCategory = subCategoryRepo.findByName(subCategoryName);
		inventoryValidator.validate(inventory, subCategory);
		
		inventory.setId(null);
		inventory.setSubCategory(subCategory);
				
		return inventoryRepo.save(inventory);
	}

	
	@Override
	public Inventory updateInventory(Inventory inventory) throws ValidationException {
		logger.info("update inventory: " + inventory);		
		
		Integer id = inventory.getId();
		
		Inventory existingInventory = inventoryRepo.findById(id)
			       					  			   .orElseThrow(() -> new ResourceNotFoundException(String.format(INVENTORY_NOT_FOUND, id)));
		
		int quantity =  inventory.getQuantity();
		inventoryValidator.validateQuantity(quantity);
		existingInventory.setQuantity(quantity);
		return inventoryRepo.save(existingInventory);		
	}	

	@Override
	public Inventory getInventory(int id) {
		logger.info("getInventory id = " + id);	
		return inventoryRepo.findById(id).orElseGet(() -> {return null;});
	}
	
	@Override
	public List<Inventory> findAll() {
		List<Inventory> inventoryList = inventoryRepo.findAll();
		Collections.sort(inventoryList);
		return inventoryList;
	}

	@Override
	public void deleteInventory(int id) {
		logger.info("delete inventory id: " + id);
		
		Inventory inventory = inventoryRepo.findById(id)
					  					   .orElseThrow(() -> new ResourceNotFoundException(String.format(INVENTORY_NOT_FOUND, id)));
		inventoryRepo.delete(inventory);
	}

}