package anson.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import anson.inventory.entity.SubCategory;
import anson.inventory.entity.SubCategoryPk;

@Repository
@Transactional
public interface SubCategoryRepository extends JpaRepository<SubCategory, SubCategoryPk> {

	SubCategory findByName(String name);
	
	@Query("SELECT subCat FROM SubCategory subCat WHERE subCat.subCategoryPk.category.name=(:category)")
    SubCategory findByCategory(@Param("category") String category);	

	@Query("SELECT subCat.name FROM SubCategory subCat order by subCat.name")
    List<String> findAllNames();
	
}