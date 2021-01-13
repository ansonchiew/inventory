package anson.inventory.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import anson.inventory.repository.SubCategoryRepository;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

	Logger logger = LoggerFactory.getLogger(SubCategoryServiceImpl.class);

	@Autowired
    private SubCategoryRepository subCategoryRepo;
	
	public SubCategoryRepository getSubCategoryRepo() {
		return subCategoryRepo;
	}

	public void setSubCategoryRepo(SubCategoryRepository subCategoryRepo) {
		this.subCategoryRepo = subCategoryRepo;
	}

	@Override
	public List<String> findAllNames() {
		logger.info("find all categories");
		return subCategoryRepo.findAllNames();
	}

}