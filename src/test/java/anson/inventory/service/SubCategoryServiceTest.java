package anson.inventory.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import anson.inventory.Application;
import anson.inventory.repository.SubCategoryRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={Application.class})
@WebAppConfiguration
public class SubCategoryServiceTest {
	
	@Autowired
	private SubCategoryService subCategoryService;
	
	@MockBean	
	private SubCategoryRepository subCategoryRepo;
	
	@Test
	public void findAllNames() {
		String subCategory1Name = "Cup Cake";
		String subCategory2Name = "Shirt";
		
		List<String> expectedList = Arrays.asList(new String[]{subCategory1Name, subCategory2Name});
		
		when(subCategoryRepo.findAllNames()).thenReturn(expectedList);
		
		assertThat(subCategoryService.findAllNames(), equalTo(expectedList));			
	}

}