package anson.inventory.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import anson.inventory.entity.SubCategory;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SubCategoryRepositoryTest {
	
	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Test
	public void findByCategory() {
		String category = "Food";
		String expectedSubcategpryName = "Cake";
			
		SubCategory subCategory = subCategoryRepository.findByCategory(category);
		if (subCategory != null) {
			assertThat(subCategory.getName(), equalTo(expectedSubcategpryName));
		} else {
			fail("No sub-category found in Category " + category);
		}
	}
	
	@Test
	public void findAllNames() {
		String subCategory1Name = "Cake";
		String subCategory2Name = "Shirt";
		List<String> subCategoryList = Arrays.asList(new String[]{subCategory1Name, subCategory2Name});
	
		assertThat(subCategoryRepository.findAllNames(), equalTo(subCategoryList));		
	}

}
