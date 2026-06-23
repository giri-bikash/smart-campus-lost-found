package com.binaryboys.lostfound.repository;

import com.binaryboys.lostfound.model.Item;
import com.binaryboys.lostfound.model.Item.Category;
import com.binaryboys.lostfound.model.Item.ItemType;
import com.binaryboys.lostfound.model.Item.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByType(ItemType type);
    List<Item> findByTypeAndStatus(ItemType type, Status status);
    List<Item> findByTypeAndStatusAndCategory(ItemType type, Status status, Category category);
    List<Item> findByUserId(Long userId);
}