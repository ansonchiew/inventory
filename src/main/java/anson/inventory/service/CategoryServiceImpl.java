package anson.inventory.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import anson.inventory.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired
    private CategoryRepository categoryRepo;
	
	public CategoryRepository getCategoryRepo() {
		return categoryRepo;
	}

	public void setCategoryRepo(CategoryRepository categoryRepo) {
		this.categoryRepo = categoryRepo;
	}

	@Override
	public List<String> findAllNames() {
		logger.info("find all categories");
		return categoryRepo.findAllNames();
	}

}