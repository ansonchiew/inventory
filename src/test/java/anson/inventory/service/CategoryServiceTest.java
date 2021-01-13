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
import anson.inventory.repository.CategoryRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={Application.class})
@WebAppConfiguration
public class CategoryServiceTest {

	@Autowired
	private CategoryService categoryService;
	
	@MockBean	
	private CategoryRepository categoryRepo;
	
	@Test
	public void findAllNames() {
		String category1Name = "Clothes";
		String category2Name = "Food";
		
		List<String> expectedList = Arrays.asList(new String[]{category1Name, category2Name});
		
		when(categoryRepo.findAllNames()).thenReturn(expectedList);
		
		assertThat(categoryService.findAllNames(), equalTo(expectedList));			
	}
	
}