package com.example.shopapp.repositories;

import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR :keyword='' OR p.name LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR :categoryId=0 OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR :minPrice=0 OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR :maxPrice=0 OR p.price <= :maxPrice)")
    Page<Product> findAll(String keyword,
                          Long categoryId,
                          Float minPrice,
                          Float maxPrice, Pageable pageable);
    @Query("SELECT count(p) FROM Product p WHERE " +
            "(:keyword IS NULL OR :keyword='' OR p.name LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR :categoryId=0 OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR :minPrice=0 OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR :maxPrice=0 OR p.price <= :maxPrice)")
    Long countFilterProducts(String keyword,
                             Long categoryId,
                             Float minPrice,
                             Float maxPrice);
    List<Product> findByCategory_Id(Long id);
}