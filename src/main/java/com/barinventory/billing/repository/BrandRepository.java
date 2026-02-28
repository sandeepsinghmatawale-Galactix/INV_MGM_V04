package com.barinventory.billing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barinventory.billing.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
	List<Brand> findByActiveTrue();

	List<Brand> findByCategoryAndActiveTrue(Brand.Category category);

	// For manageSizes page (GET brand by id)
	Optional<Brand> findByIdAndActiveTrue(Long id);

	// Check duplicate name (admin create/update)
	boolean existsByNameIgnoreCase(String name);
	

	
	@Query("""
		       SELECT b FROM Brand b
		       LEFT JOIN FETCH b.sizes
		       WHERE b.id = :id
		       """)
		Optional<Brand> findByIdWithSizes(@Param("id") Long id);
	
	@Query("""
		    SELECT DISTINCT b FROM Brand b
		    LEFT JOIN FETCH b.sizes s
		    WHERE b.active = true
		""")
		List<Brand> findAllActiveWithSizes();
	
	
}