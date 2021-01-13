package anson.inventory.controller;

import static anson.inventory.controller.inventoryController.INSTRUCTION;
import static anson.inventory.controller.inventoryController.HOME_PAGE;
import static anson.inventory.controller.inventoryController.INVENTORY_FORM;
import static anson.inventory.controller.inventoryController.INVENTORY_BROWSE;
import static anson.inventory.controller.inventoryController.INVENTORY_LIST;

import static anson.inventory.controller.inventoryController.CREATE_INVENTORY;
import static anson.inventory.controller.inventoryController.EDIT_INVENTORY;

import static anson.inventory.controller.inventoryController.ACTION_MSG;
import static anson.inventory.controller.inventoryController.DELETE_MSG;

import static anson.inventory.controller.inventoryController.ACTION;
import static anson.inventory.controller.inventoryController.NEW;
import static anson.inventory.controller.inventoryController.EDIT;
import static anson.inventory.controller.inventoryController.BROWSE;
import static anson.inventory.controller.inventoryController.DELETE;
import static anson.inventory.controller.inventoryController.CREATED;
import static anson.inventory.controller.inventoryController.UPDATED;

import static anson.inventory.service.InventoryServiceImpl.INVENTORY_NOT_FOUND;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.WordUtils;
import org.junit.Test;
import org.junit.runner.RunWith;    

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import anson.inventory.Application;
import anson.inventory.entity.Category;
import anson.inventory.entity.Inventory;
import anson.inventory.entity.SubCategory;
import anson.inventory.entity.SubCategoryPk;
import anson.inventory.exception.ValidationException;
import anson.inventory.service.CategoryService;
import anson.inventory.service.InventoryService;
import anson.inventory.service.SubCategoryService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = inventoryController.class)
@ContextConfiguration(classes={Application.class})
public class InventoryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private InventoryService inventoryService;
	
	@MockBean
	private CategoryService categoryService;
	
	@MockBean
	private SubCategoryService subCategoryService;
	
	@SpyBean
	private inventoryController inventoryController;
	
	@Test
	public void home() throws Exception {
		String requestPath = "/inventory/home";
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(requestPath)
				   						.accept(MediaType.TEXT_HTML);
		
		mockMvc.perform(requestBuilder).andExpect(model().attribute("message", INSTRUCTION))
									   .andExpect(view().name(HOME_PAGE))
									   .andExpect(status().isOk());
	}	
	
	@Test
	public void createInventoryForm() throws Exception {
		String category1Name = "Clothes";
		String category2Name = "Food";
		List<String> categoryList = Arrays.asList(new String[]{category1Name, category2Name});
		
		String subCategory1Name = "Cake";
		String subCategory2Name = "Shirt";
		List<String> subCategoryList = Arrays.asList(new String[]{subCategory1Name, subCategory2Name});
		 
		doReturn(categoryList).when(inventoryController).getCategoryList();
		doReturn(subCategoryList).when(inventoryController).getSubCategoryList();
		
		String requestPath = "/inventory?" + NEW;
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(requestPath)
				   						.accept(MediaType.TEXT_HTML);
		
		mockMvc.perform(requestBuilder).andExpect(model().attribute("title", CREATE_INVENTORY))
									   .andExpect(model().attribute("method", HttpMethod.POST))
									   .andExpect(model().attribute("edit", "false"))
									   .andExpect(model().attribute("inventory", new Inventory()))
									   .andExpect(model().attribute("categoryList", categoryList))
									   .andExpect(model().attribute("subCategoryList", subCategoryList))
									   .andExpect(view().name(INVENTORY_FORM))
									   .andExpect(status().isOk());													
	}
	
	@Test
	public void createInventorySuccessfully() throws Exception {
		String category = "Food";		
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 5;
		int inventoryId = 1;
	
		Inventory inventory = createInventory(category, subCategory, name, quantity);
		
	    Inventory savedInventory = createInventory(subCategory, category, name, quantity);
		savedInventory.setId(inventoryId);
		
		when(inventoryService.createInventory(inventory)).thenReturn(savedInventory);
		
		String requestPath = "/inventory?" + NEW;
		String redirectedUrl = "http://localhost/inventory/" + inventoryId + "?" + ACTION + "=" + CREATED;
				
		RequestBuilder requestBuilder = getRequestBuilder(requestPath, inventory);
	
		mockMvc.perform(requestBuilder).andExpect(model().size(0))
									   .andExpect(status().isFound())
									   .andExpect(redirectedUrl(redirectedUrl));													
	}

	@Test
	public void createInventoryWithNonNumericQuantity() throws Exception {
		String category = "Food";		
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 0;
	
		Inventory inventory = createInventory(category, subCategory, name, quantity);
		String requestPath = "/inventory?" + NEW;
		
		RequestBuilder requestBuilder = getRequestBuilder(requestPath, inventory, false);
		
		mockMvc.perform(requestBuilder).andExpect(model().hasErrors())
	    							   .andExpect(model().attributeHasFieldErrors("inventory", "quantity"))
	    							   .andExpect(model().attributeHasFieldErrorCode("inventory", "quantity", "typeMismatch"))
	    							   .andExpect(view().name(INVENTORY_FORM))
									   .andExpect(status().isOk());													
	}

	@Test
	public void createInventoryAndThrowValidationExcpetion() throws Exception {
		String category = "Food";		
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 5;
		String msg = "Validation Exception";

		Inventory inventory = createInventory(category, subCategory, name, quantity);
    		
		ValidationException validationException = new ValidationException(msg);
		when(inventoryService.createInventory(inventory)).thenThrow(validationException);

		String requestPath = "/inventory?" + NEW;

		RequestBuilder requestBuilder = getRequestBuilder(requestPath, inventory);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("error", validationException.getMessage()))
									   .andExpect(view().name(INVENTORY_FORM))
									   .andExpect(status().isOk());							
	}
		
	@Test
	public void editInventoryFormWithExistingInventory() throws Exception {
		String category = "Food";
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 5;
		int inventoryId = 1;

		Inventory inventory = createInventory(category, subCategory, name, quantity);
		inventory.setId(inventoryId);
		
		String requestPath = "/inventory/" + inventoryId + "?"  + ACTION + "=" + EDIT;
		
		when(inventoryService.getInventory(inventoryId)).thenReturn(inventory);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(requestPath)
										.accept(MediaType.TEXT_HTML);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("title", EDIT_INVENTORY))
									   .andExpect(model().attribute("method", HttpMethod.POST))
									   .andExpect(model().attribute("edit", "true"))
									   .andExpect(model().attribute("inventory", inventory))
									   .andExpect(view().name(INVENTORY_FORM))
									   .andExpect(status().isOk());													
									
	}
	
	@Test
	public void editInventoryFormWithNonExistingInventory() throws Exception {
		int inventoryId = 1;
		String requestPath = "/inventory/" + inventoryId + "?" + ACTION + "=" + EDIT;
		
		when(inventoryService.getInventory(inventoryId)).thenReturn(null);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(requestPath)
									  	.accept(MediaType.TEXT_HTML);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("error", INVENTORY_NOT_FOUND + inventoryId))
									   .andExpect(view().name(HOME_PAGE))
									   .andExpect(status().isOk());							
	}	
	
	@Test
	public void updateInventorySuccessfully() throws Exception {
		String category = "Food";
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 5;
		int inventoryId = 1;
					
		Inventory inventory = createInventory(category, subCategory, name, quantity);
		inventory.setId(inventoryId);
	    		
		when(inventoryService.updateInventory(inventory)).thenReturn(inventory);

		String requestPath = "/inventory/" + inventoryId + "?" + ACTION + "=" + EDIT;
		String redirectedUrl = "http://localhost/inventory/" + inventoryId + "?" + ACTION + "=" + UPDATED;

		RequestBuilder requestBuilder = getRequestBuilder(requestPath, inventory);

		
		mockMvc.perform(requestBuilder).andExpect(model().size(0))
									   .andExpect(status().isFound())
									   .andExpect(redirectedUrl(redirectedUrl));													
	}
	
	@Test
	public void updateInventoryWithNonNumericQuantity() throws Exception {
		String category = "Food";		
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 0;
		int inventoryId = 1;
		
		Inventory inventory = createInventory(category, subCategory, name, quantity);
		inventory.setId(inventoryId);
	    
		String requestPath = "/inventory/" + inventoryId + "?action=edit";
		
		RequestBuilder requestBuilder = getRequestBuilder(requestPath, inventory, false);
		
		mockMvc.perform(requestBuilder).andExpect(model().hasErrors())
	    							   .andExpect(model().attributeHasFieldErrors("inventory", "quantity"))
	    							   .andExpect(model().attributeHasFieldErrorCode("inventory", "quantity", "typeMismatch"))
	    							   .andExpect(view().name(INVENTORY_FORM))
									   .andExpect(status().isOk());													
	}

	@Test
	public void updateNonExistingInventoryAndThrowResourceNotFoundException() throws Exception {
		String category = "Food";
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 5;
		int inventoryId = 1;
		
		Inventory inventory = createInventory(category, subCategory, name, quantity);
		inventory.setId(inventoryId);
	    		
		ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(INVENTORY_NOT_FOUND + inventoryId);
		when(inventoryService.updateInventory(inventory)).thenThrow(resourceNotFoundException);
	
		String requestPath = "/inventory/" + inventoryId + "?" + ACTION + "=" + EDIT;

		RequestBuilder requestBuilder = getRequestBuilder(requestPath, inventory);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("error", resourceNotFoundException.getMessage()))
									   .andExpect(view().name(INVENTORY_FORM))
									   .andExpect(status().isOk());							
	}

	@Test
	public void updateInventoryAndThrowValidationException() throws Exception {
		String category = "Food";
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = -5;
		int inventoryId = 1;
		String msg = "Validation Exception";

		Inventory inventory = createInventory(category, subCategory, name, quantity);
		inventory.setId(inventoryId);
	
		ValidationException validationException = new ValidationException(msg);
		when(inventoryService.updateInventory(inventory)).thenThrow(validationException);

		String requestPath = "/inventory/" + inventoryId + "?" + ACTION + "=" + EDIT;

		RequestBuilder requestBuilder = getRequestBuilder(requestPath, inventory);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("error", validationException.getMessage()))
									   .andExpect(view().name(INVENTORY_FORM))
									   .andExpect(status().isOk());							
	}
	
	
	private RequestBuilder getRequestBuilder(String path, Inventory inventory) {
		return getRequestBuilder(path, inventory, true);
	}
 		
	private RequestBuilder getRequestBuilder(String path, Inventory inventory, boolean numericQuantity) {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(path)
													   .accept(MediaType.TEXT_HTML)
													   .param("name", inventory.getName())
													   .param("category.name", inventory.getCategory().getName())
													   .param("subCategory.name", inventory.getSubCategory().getName());
		if (inventory.getId() != null) {
			requestBuilder = requestBuilder.param("id", String.valueOf(inventory.getId()));					
		}
		
		if (numericQuantity) {
			requestBuilder = requestBuilder.param("quantity", String.valueOf(inventory.getQuantity()));					
		} else {
			requestBuilder = requestBuilder.param("quantity", "abc");
		}
		return requestBuilder.contentType(MediaType.APPLICATION_FORM_URLENCODED);		
	}

	@Test
	public void getNonExistingInventoryById() throws Exception {
		int inventoryId = 1;
			
		when(inventoryService.getInventory(inventoryId)).thenReturn(null);
	
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/"+inventoryId)
															  .accept(MediaType.TEXT_HTML);
	
		mockMvc.perform(requestBuilder).andExpect(model().attribute("error", String.format(INVENTORY_NOT_FOUND, inventoryId)))
									   .andExpect(view().name(HOME_PAGE))
									   .andExpect(status().isOk());							
	}
	
	@Test
	public void getCreatedInventoryById() throws Exception {
		getInventoryAfterAction(CREATED);
	}
	
	@Test
	public void getUpdatedInventoryById() throws Exception {
		getInventoryAfterAction(UPDATED);
	}

	@Test
	public void getInventoryById() throws Exception {
		getInventoryAfterAction(BROWSE);
	}
	
	private void getInventoryAfterAction(String action) throws Exception {
		String title = "";
		if (action.equalsIgnoreCase(BROWSE)) {
			title = WordUtils.capitalize(action) + " Inventory";
		} else {
			title = String.format(ACTION_MSG, action);
		}
		
		String category = "Food";
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 5;
		int inventoryId = 1;
		
		Inventory inventory = createInventory(category, subCategory, name, quantity);
		inventory.setId(inventoryId);
		
		Map<Integer, String> inventoryMap = new HashMap<>();
		inventoryMap.put(1, "Cup Cake");
		doReturn(inventoryMap).when(inventoryController).getInventoryMap();
		
		when(inventoryService.getInventory(inventoryId)).thenReturn(inventory);
				
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/"+inventoryId)
															  .accept(MediaType.TEXT_HTML)
															  .param(ACTION, action);
				   
		mockMvc.perform(requestBuilder).andExpect(model().attribute("title", title))
									   .andExpect(model().attribute("inventory", inventory))
									   .andExpect(model().attribute("inventoryList", inventoryMap))	
									   .andExpect(view().name(INVENTORY_BROWSE))
									   .andExpect(status().isOk());														
	}
	
	@Test
	public void getAllInventory() throws Exception {
		String action = BROWSE;
		Map<Integer, String> inventoryMap = new HashMap<>();
		inventoryMap.put(1, "Cup Cake");
		doReturn(inventoryMap).when(inventoryController).getInventoryMap();
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory")
															  .accept(MediaType.TEXT_HTML)
															  .param(ACTION, action);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("title", WordUtils.capitalize(action) + " Inventory"))
									   .andExpect(model().attribute("inventory", new Inventory()))
									   .andExpect(model().attribute("inventoryList", inventoryMap))	
									   .andExpect(view().name(INVENTORY_LIST))
									   .andExpect(status().isOk());														
	}

	@Test
	public void deleteExistingInventory() throws Exception {
		int inventoryId = 1;
						
		doNothing().when(inventoryService).deleteInventory(inventoryId);
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/" + inventoryId)
															  .accept(MediaType.TEXT_HTML)
															  .param(ACTION, DELETE);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("message", String.format(DELETE_MSG, inventoryId)))
		   							   .andExpect(view().name(HOME_PAGE))
		   							   .andExpect(status().isOk());														
	}	
	
	@Test
	public void deleteNonExistingInventory() throws Exception {
		int inventoryId = 1;
				
		ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(String.format(INVENTORY_NOT_FOUND, inventoryId));
		doThrow(resourceNotFoundException).when(inventoryService).deleteInventory(inventoryId);
			
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/inventory/" + inventoryId)
															  .accept(MediaType.TEXT_HTML)
															  .param(ACTION, DELETE);

		mockMvc.perform(requestBuilder).andExpect(model().attribute("error", resourceNotFoundException.getMessage()))
									   .andExpect(view().name(HOME_PAGE))
									   .andExpect(status().isOk());														
	
	}
	
	@Test
	public void getCategoryList() {
		inventoryController.getCategoryList();
		verify(categoryService, times(1)).findAllNames();
	}
	
	@Test
	public void getSubCategoryList() {
		inventoryController.getSubCategoryList();
		verify(subCategoryService, times(1)).findAllNames();
	}
	
	@Test
	public void getInventoryMap() {
		String category = "Food";
		String subCategory = "Cake";
		String name = "Cup Cake";
		int quantity = 5;
		int inventoryId = 1;
		
		Inventory inventory = createInventory(category, subCategory, name, quantity);
		inventory.setId(inventoryId);
		
		List<Inventory> inventoryList = Arrays.asList(new Inventory[]{inventory});
		
		when(inventoryService.findAll()).thenReturn(inventoryList);
		
		Map<Integer, String> expectedInventoryMap = new LinkedHashMap<>();
		expectedInventoryMap.put(inventory.getId(), inventory.getName());
		
		assertThat(inventoryController.getInventoryMap(), equalTo(expectedInventoryMap));		
	}	
	
	private Inventory createInventory(String categoryName, String subCategoryName, String inventoryName, int quantity) {
		Category category = new Category();
		category.setName(categoryName);
		SubCategoryPk subCategoryPk = new SubCategoryPk();
		subCategoryPk.setCategory(category);
		SubCategory subCategory = new SubCategory(subCategoryPk, subCategoryName);
		return new Inventory(subCategory, inventoryName, quantity);
	}	
	
}