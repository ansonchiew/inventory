package anson.inventory.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CategoryRepositoryTest {

	@Autowired
	CategoryRepository categoryRepository;
	
	@Test
	public void findAllNames() {
		String category1Name = "Clothes";
		String category2Name = "Food";
		List<String> categoryList = Arrays.asList(new String[]{category1Name, category2Name});
	
		assertThat(categoryRepository.findAllNames(), equalTo(categoryList));
	}
	
}