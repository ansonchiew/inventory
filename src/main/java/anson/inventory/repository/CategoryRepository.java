package anson.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import anson.inventory.entity.Category;

@Repository
@Transactional
public interface CategoryRepository  extends JpaRepository<Category, Integer> {

    Category findByName(String name);
	
	@Query("SELECT cat.name FROM Category cat order by cat.name")
    List<String> findAllNames();
	
}