package com.rishabh.store.inventory.repository;

import com.rishabh.store.inventory.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, String> {

    List<InventoryEntity> findByUniqIdIn(List<String> uniqIds);
}
