package anson.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import anson.inventory.entity.Inventory;

@Repository
@Transactional
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Inventory findByName(String name);

}