package anson.inventory.service;

import java.util.List;

import anson.inventory.entity.Inventory;
import anson.inventory.exception.ValidationException;

public interface InventoryService {
	
	public Inventory createInventory(Inventory inventory) throws ValidationException;
	
	public Inventory updateInventory(Inventory inventory) throws ValidationException;
	
	public Inventory getInventory(int id);
			
	public List<Inventory> findAll();
	
	public void deleteInventory(int id);
	
}