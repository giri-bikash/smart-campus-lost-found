package com.binaryboys.lostfound.repository;

import com.binaryboys.lostfound.model.ItemMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemMatchRepository extends JpaRepository<ItemMatch, Long> {
    List<ItemMatch> findByLostItemId(Long lostItemId);
    List<ItemMatch> findByFoundItemId(Long foundItemId);
    boolean existsByLostItemIdAndFoundItemId(Long lostItemId, Long foundItemId);
}