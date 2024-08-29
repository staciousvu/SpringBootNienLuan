package com.example.shopapp.services.category;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    // Lấy danh sách tất cả các danh mục
    Page<Category> getAllCategories(int page,int limit);

    Long countFilterCategories(String keyword);

    Page<Category> getAllCategory(String keyword, Pageable pageable);

    List<Category> getAllCategories();

    // Lấy danh mục theo ID
    Category getCategoryById(Long id);

    // Tạo một danh mục mới
    Category createCategory(CategoryDTO categoryDTO);

    // Cập nhật một danh mục hiện có
    Category updateCategory(Long id, CategoryDTO categoryDTO);

    // Xóa một danh mục theo ID
    void deleteCategory(Long id);
}
