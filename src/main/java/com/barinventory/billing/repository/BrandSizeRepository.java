package com.barinventory.billing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barinventory.billing.entity.Brand;
import com.barinventory.billing.entity.BrandSize;

public interface BrandSizeRepository extends JpaRepository<BrandSize, Long> {
	List<BrandSize> findByBrandIdAndActiveTrue(Long brandId);
	

    // For deleting (soft deactivate)
    Optional<BrandSize> findByIdAndActiveTrue(Long id);

    // Check duplicate size per brand
    boolean existsByBrandIdAndSizeLabelIgnoreCase(Long brandId, String sizeLabel);

    // Sorted display for UI
    List<BrandSize> findByBrandIdAndActiveTrueOrderByDisplayOrderAsc(Long brandId);
	
    @Query("""
    	       SELECT b FROM Brand b
    	       LEFT JOIN FETCH b.sizes
    	       WHERE b.id = :id
    	       """)
    	Optional<Brand> findByIdWithSizes(@Param("id") Long id);
    
    @Query("""
    	    SELECT DISTINCT b FROM Brand b
    	    LEFT JOIN FETCH b.sizes s
    	    WHERE b.active = true AND (s.active = true OR s IS NULL)
    	    ORDER BY b.name
    	""")
    	List<Brand> findAllActiveWithSizes();
    
}