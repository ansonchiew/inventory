package anson.inventory.controller;

import static anson.inventory.service.InventoryServiceImpl.INVENTORY_NOT_FOUND;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import anson.inventory.entity.Inventory;
import anson.inventory.exception.ValidationException;
import anson.inventory.service.CategoryService;
import anson.inventory.service.InventoryService;
import anson.inventory.service.SubCategoryService;

@Controller
@SessionAttributes(value = {"title", "inventory", "inventoryList", "categoryList", "subCategoryList", "method", "edit"})
@RequestMapping("/inventory")
public class inventoryController {

	Logger logger = LoggerFactory.getLogger(inventoryController.class);
	
	public static final String INSTRUCTION = "Please select a menu item to proceed.";
	public static final String HOME_PAGE = "homePage";
	public static final String INVENTORY_FORM = "inventoryForm";
	public static final String INVENTORY_BROWSE = "inventoryBrowse";
	public static final String INVENTORY_LIST = "inventoryList";
	
	public static final String CREATE_INVENTORY = "Create Inventory";
	public static final String EDIT_INVENTORY = "Edit Inventory";
	
	public static final String ACTION_MSG = "The following inventory has been %s:";
	public static final String DELETE_MSG = "Inventory ID: %d has been deleted successfully.";
	
	public static final String ACTION = "action";
	public static final String NEW = "new";
	public static final String EDIT = "edit";
	public static final String BROWSE = "browse";
	public static final String DELETE = "delete";
	public static final String CREATED = "created";
	public static final String UPDATED = "updated";
		
	@Autowired
    private InventoryService inventoryService;
	
	@Autowired
    private CategoryService categoryService;
	
	@Autowired
    private SubCategoryService subCategoryService;
		
	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public SubCategoryService getSubCategoryService() {
		return subCategoryService;
	}

	public void setSubCategoryService(SubCategoryService subCategoryService) {
		this.subCategoryService = subCategoryService;
	}

	@InitBinder(value="inventory") 
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
		
	@GetMapping("/home")
	public ModelAndView home() {		
		return new ModelAndView(HOME_PAGE, "message", INSTRUCTION);
	}
	
	@GetMapping(params=NEW)
	public String createInventoryForm(Model model) {
		model.addAttribute("title", CREATE_INVENTORY);
		model.addAttribute("method", HttpMethod.POST);
		model.addAttribute("edit", "false");
		model.addAttribute(new Inventory());		
		model.addAttribute("categoryList", getCategoryList());
		model.addAttribute("subCategoryList", getSubCategoryList());
		return INVENTORY_FORM;
	}
			
	@PostMapping
    public String createInventory(Inventory inventory, BindingResult bindingResult, Model model, SessionStatus sessionStatus) {
		logger.info("create inventory: " + inventory);
		
		if (bindingResult.hasErrors()){
			logger.error(bindingResult.getAllErrors().toString());
			return INVENTORY_FORM;
		}
		
		Inventory savedInventory = null;
		try {
			savedInventory = inventoryService.createInventory(inventory);
		} catch (ValidationException ex) {
			logger.error(ex.getMessage());
			model.addAttribute("error", ex.getMessage());
			return INVENTORY_FORM;
		}
		
		sessionStatus.setComplete();
		model.asMap().clear();
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().replaceQuery(ACTION + "=" + CREATED).path("/{id}").buildAndExpand(savedInventory.getId()).toUri();
		logger.info("location: " + location);
		return "redirect:" + location;
	}
	
	@GetMapping(value="/{id}", params=ACTION + "=" + EDIT)
	public String editInventoryForm(@PathVariable("id") Integer id, Model model) {
		logger.info("edit inventory id: " + id);		
		Inventory inventory = inventoryService.getInventory(id);
		if (inventory == null) {
			logger.error(INVENTORY_NOT_FOUND + id);
			model.addAttribute("error", INVENTORY_NOT_FOUND + id);
			return HOME_PAGE;
		}
		
		model.addAttribute("title", EDIT_INVENTORY);
		model.addAttribute("method", HttpMethod.POST);
		model.addAttribute("edit", "true");
		model.addAttribute(inventory);
		
		return INVENTORY_FORM;		
	}
		
	@PostMapping(value="/{id}", params=ACTION + "=" + EDIT)
	public String updateInventory(@PathVariable("id") Integer id, Inventory inventory, BindingResult bindingResult, Model model, SessionStatus sessionStatus) {
		logger.info("update inventory: " + inventory);		
		
		if (bindingResult.hasErrors()){			
			return INVENTORY_FORM;
		}
		
		try {
			inventory = inventoryService.updateInventory(inventory);
		} catch (ResourceNotFoundException | ValidationException ex) {
			logger.error(ex.getMessage());
			model.addAttribute("error", ex.getMessage());
			return INVENTORY_FORM;
		}
		
		sessionStatus.setComplete();
		model.asMap().clear();		
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam(ACTION, UPDATED).build().toUri();
		return "redirect:" + location;	
	}
	
	@GetMapping("/{id}")
	public String getInventory(@PathVariable("id") Integer id, Model model, @RequestParam(value=ACTION, required = false) String action) {
		logger.info("getInventory id = " + id);
		Inventory inventory = inventoryService.getInventory(id);
		if (inventory == null) {
			logger.error(String.format(INVENTORY_NOT_FOUND, id));
			model.addAttribute("error", String.format(INVENTORY_NOT_FOUND, id));
			return HOME_PAGE;
		}
		
		String title = "";
		if (action.equalsIgnoreCase(CREATED) || action.equalsIgnoreCase(UPDATED)){
			title = String.format(ACTION_MSG, action);
		} else if (action.equalsIgnoreCase(BROWSE)) {
			title = WordUtils.capitalize(action) + " Inventory";
		}
		
		model.addAttribute("title", title);		
		model.addAttribute(inventory);
		model.addAttribute("inventoryList", getInventoryMap());
		
		return INVENTORY_BROWSE;
	}
		
	@GetMapping(value="/{id}", params=ACTION + "=" + DELETE)
	public String deleteInventory(@PathVariable("id") Integer id, Model model, SessionStatus sessionStatus) {
		logger.info("delete inventory id: " + id);		
		
		sessionStatus.setComplete();
		model.asMap().clear();		

		try {			
			inventoryService.deleteInventory(id);
		} catch (ResourceNotFoundException ex) {
			logger.error(ex.getMessage());
			model.addAttribute("error", ex.getMessage());
			return HOME_PAGE;
		}
				
		model.addAttribute("message", String.format(DELETE_MSG, id));
		return HOME_PAGE;	
	}
	
	@GetMapping
	public String getAllInventory(Model model, @RequestParam(value=ACTION) String action){
		String title = WordUtils.capitalize(action) + " Inventory";
		model.addAttribute("title", title);
		model.addAttribute("inventoryList", getInventoryMap());
		model.addAttribute(new Inventory());
				
		return INVENTORY_LIST;	
	}

	List<String> getCategoryList() {
		logger.info("find all category names");
		return categoryService.findAllNames();
	}
	
	List<String> getSubCategoryList() {
		logger.info("find all sub-category names");
		return subCategoryService.findAllNames();
	}

	Map<Integer, String> getInventoryMap() {
		logger.info("find all inventory names");
		return inventoryService.findAll().stream()
			   .collect(LinkedHashMap::new, (map, item) -> map.put(item.getId(), item.getName()), Map::putAll); 				
	}
	
}