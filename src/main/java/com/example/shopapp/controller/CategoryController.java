package com.example.shopapp.controller;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import com.example.shopapp.responses.CategoryListResponse;
import com.example.shopapp.responses.CategoryResponse;
import com.example.shopapp.responses.ProductListResponse;
import com.example.shopapp.responses.ProductResponse;
import com.example.shopapp.services.category.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
//@Validated
public class CategoryController {
    private final CategoryService service;
    private final ModelMapper modelMapper;

    @GetMapping("/all") //http://localhost:8080/api/v1/categories?page=1&limit=5
    public ResponseEntity<Page<Category>> getAllCategories(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit){
        Page<Category> categories=service.getAllCategories(page,limit);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/SearchPagination") //http://localhost:8080/api/v1/products?page=1&limit=5
    public ResponseEntity<CategoryListResponse> getCategoriesPage(@RequestParam(value = "keyword",required = false) String keyword,
                                                                @RequestParam("page") int page,
                                                                @RequestParam("limit") int limit,
                                                                HttpServletRequest request) {

        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<Category> categoryPage = service.getAllCategory(keyword,pageRequest);
        Long count=service.countFilterCategories(keyword);
        List<CategoryResponse> categoryResponses = categoryPage.stream().map(category -> {
            return modelMapper.map(category, CategoryResponse.class);
        }).toList();
        CategoryListResponse categoryListResponse = CategoryListResponse.builder()
                .categoryResponses(categoryResponses)
                .totalPages(categoryPage.getTotalPages())
                .count(count)
                .build();
        return ResponseEntity.ok(categoryListResponse);

    }



    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories=service.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(service.getCategoryById(id));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 BindingResult result){
        if (result.hasErrors()){
            List<FieldError> errorList=result.getFieldErrors();
            List<String> list=errorList.stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(list);
        }

        return ResponseEntity.ok(service.createCategory(categoryDTO));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,@Valid @RequestBody CategoryDTO categoryDTO){
        try {
            return ResponseEntity.ok(service.updateCategory(id,categoryDTO));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id){
        try {
            service.deleteCategory(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
