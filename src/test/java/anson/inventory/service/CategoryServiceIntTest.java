package anson.inventory.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import anson.inventory.config.TilesConfiguration;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = "anson.inventory",  
	           excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TilesConfiguration.class}))
@DataJpaTest
@WebAppConfiguration
public class CategoryServiceIntTest {

	@Autowired
	private CategoryService categoryService;

	@Test
	public void findAllNames() {
		String category1Name = "Clothes";
		String category2Name = "Food";
			
		List<String> expectedList = Arrays.asList(new String[]{category1Name, category2Name});
		
		assertThat(categoryService.findAllNames(), equalTo(expectedList));			
	}
	
}