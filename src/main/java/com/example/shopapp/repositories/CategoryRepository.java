package com.example.shopapp.repositories;

import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAll(Pageable pageable);
    boolean existsById(Long id);
    @Query("SELECT c FROM Category c WHERE " +
            "(:keyword IS NULL OR :keyword='' OR c.name LIKE %:keyword%)")
    Page<Category> findAll(String keyword,
                          Pageable pageable);
    @Query("SELECT count(c) FROM Category c WHERE " +
            "(:keyword IS NULL OR :keyword='' OR c.name LIKE %:keyword%)")
    Long countFilterCategories(String keyword);
}
