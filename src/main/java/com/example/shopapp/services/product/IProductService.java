package com.example.shopapp.services.product;

import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.dtos.ProductImageDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    List<Product> getAllProduct();
    Long countFilterProducts(String keyword,
                             Long categoryId,
                             Float minPrice,
                             Float maxPrice);

    Page<Product> getAllProduct(String keyword,
                                Long categoryId,
                                Float minPrice,
                                Float maxPrice, Pageable pageable);

    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    void deleteProduct(Long id) throws DataNotFoundException;
    Product updateProduct(Long id,ProductDTO productDTO) throws DataNotFoundException;
    List<Product> getProductByCategory(Long categoryId) throws DataNotFoundException;
    Product getProductById(Long id) throws DataNotFoundException;
    boolean existByName(String name);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException;
    String storeFile(MultipartFile multipartFile) throws IOException;
}
