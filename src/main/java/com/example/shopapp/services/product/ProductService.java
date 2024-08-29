package com.example.shopapp.services.product;

import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.dtos.ProductImageDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.repositories.ProductImageRepository;
import com.example.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ProductImageRepository productImageRepository;

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Long countFilterProducts(String keyword, Long categoryId, Float minPrice, Float maxPrice) {
        return productRepository.countFilterProducts(keyword, categoryId, minPrice, maxPrice);
    }

    @Override
    public Page<Product> getAllProduct(String keyword,
                                       Long categoryId,
                                       Float minPrice,
                                       Float maxPrice, Pageable pageable) {
        Page<Product> productPage=productRepository.findAll(keyword,categoryId,minPrice,maxPrice,pageable);
        return productPage;
    }

    @Transactional
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(category)
                .build();
        return productRepository.save(newProduct);
    }


    @Transactional
    @Override
    public void deleteProduct(Long id) throws DataNotFoundException {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new DataNotFoundException("Product not found");
        }

    }

    @Transactional
    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existProduct = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        existProduct.setName(productDTO.getName());
        existProduct.setPrice(productDTO.getPrice());
        existProduct.setThumbnail(productDTO.getThumbnail());
        existProduct.setDescription(productDTO.getDescription());
        existProduct.setCategory(category);
        return productRepository.save(existProduct);
    }

    @Override
    public List<Product> getProductByCategory(Long categoryId) throws DataNotFoundException {
        if (categoryRepository.existsById(categoryId)) {
            return productRepository.findByCategory_Id(categoryId);
        } else throw new DataNotFoundException("Category not found");
    }

    @Override
    public Product getProductById(Long id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
    }

    @Override
    public boolean existByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO)
            throws DataNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        if (productImageRepository.findByProductId(productId).size() >= 5) {
            product.setThumbnail(productImageDTO.getImageUrl());
        }
        if (product.getThumbnail() == null || product.getThumbnail().isEmpty()) {
            product.setThumbnail(productImageDTO.getImageUrl());
        }
        productRepository.save(product);

        return productImageRepository.save(productImage);
    }

    @Override
    public String storeFile(MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        Path uploadDir = Paths.get("uploads/product");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(multipartFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

}
