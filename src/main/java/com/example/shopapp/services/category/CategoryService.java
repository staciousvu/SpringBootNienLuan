package com.example.shopapp.services.category;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category=convertToEntity(categoryDTO);
        return categoryRepository.save(category);
    }
    @Override
    public Page<Category> getAllCategories(int page, int limit) {
        Pageable pageable = PageRequest.of(page,limit);
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Long countFilterCategories(String keyword) {
        return categoryRepository.countFilterCategories(keyword);
    }

    @Override
    public Page<Category> getAllCategory(String keyword, Pageable pageable) {
        Page<Category> pageCategory=categoryRepository.findAll(keyword,pageable);
        return pageCategory;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found"));
    }

    @Transactional
    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory=categoryRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Category not found"));
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);
    }
    @Transactional
    @Override
    public void deleteCategory(Long id) {

        if(categoryRepository.existsById(id)){
            if (productRepository.findByCategory_Id(id).isEmpty()){
                categoryRepository.deleteById(id);
            }
            else {
                throw new RuntimeException("Can not be delete!");
            }
        }else {
            throw new RuntimeException("Category not found");
        }

    }
    public Category convertToEntity(CategoryDTO categoryDTO){
        return Category.builder().name(categoryDTO.getName()).build();
    }
}
