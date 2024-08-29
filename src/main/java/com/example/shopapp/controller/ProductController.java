package com.example.shopapp.controller;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.dtos.ProductImageDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.responses.ProductListResponse;
import com.example.shopapp.responses.ProductResponse;
import com.example.shopapp.services.product.IProductService;
import com.example.shopapp.services.product.ProductService;
import com.github.javafaker.Faker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final ModelMapper modelMapper;

    @PostMapping(value = "")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<FieldError> errorList = result.getFieldErrors();
                List<String> list = errorList.stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(list);
            }

            Product newProduct = productService.createProduct(productDTO);

            return ResponseEntity.ok(newProduct);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadImages(@PathVariable Long id
            , @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Product product = productService.getProductById(id);
            List<ProductImage> productImages = new ArrayList<>();
            files = (files == null) ? new ArrayList<MultipartFile>() : files;
            for (MultipartFile file : files) {
                if (file.getSize() == 0) { 
                    continue;
                }
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large! Maximum size is 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image!");
                }
                String fileName = productService.storeFile(file);
                ProductImageDTO productImageDTO = ProductImageDTO.builder()
                        .productId(id)
                        .imageUrl(fileName)
                        .build();
                ProductImage productImage = productService.createProductImage(id, productImageDTO);
                productImages.add(productImage);
            }
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all") //http://localhost:8080/api/v1/products?page=1&limit=5
    public ResponseEntity<ProductListResponse> getProductsPage(@RequestParam(value = "keyword",required = false) String keyword,
                                                               @RequestParam(value = "categoryId",required = false) Long categoryId,
                                                               @RequestParam(value = "minPrice",required = false) Float minPrice,
                                                               @RequestParam(value = "maxPrice",required = false) Float maxPrice,
                                                               @RequestParam("page") int page,
                                                               @RequestParam("limit") int limit,
                                                               HttpServletRequest request) {

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<Product> productPage = productService.getAllProduct(keyword,categoryId,minPrice,maxPrice,pageRequest);
        Long count=productService.countFilterProducts(keyword,categoryId,minPrice,maxPrice);
        List<ProductResponse> productResponses = productPage.stream().map(product -> {
            return modelMapper.map(product, ProductResponse.class);
        }).toList();
        ProductListResponse productListResponse = ProductListResponse.builder()
                .productResponses(productResponses)
                .totalPages(productPage.getTotalPages())
                .count(count)
                .build();
        return ResponseEntity.ok(productListResponse);

    }

    @GetMapping("") //http://localhost:8080/api/v1/products?page=1&limit=5
    public ResponseEntity<List<ProductResponse>> getProducts() {
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : productService.getAllProduct()) {
            ProductResponse productResponse = ProductResponse.convertToProductResponse(product);
            productResponses.add(productResponse);
        }
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductByID(@PathVariable("id") Long productID) {
        try {
            return ResponseEntity.ok(ProductResponse.convertToProductResponse(productService.getProductById(productID)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProductByID(@PathVariable("id") Long productID) {
        try {
            productService.deleteProduct(productID);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //@SecurityRequirement(name="bearer-key")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/generateFakeData")
    public ResponseEntity<String> createProductData() {
        Faker faker = new Faker();
        for (int i = 0; i < 10000; i++) {
//            String name=faker.commerce().productName();
//            if (productService.existByName(name)){
//                continue;
//            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(faker.commerce().productName())
                    .price(Float.parseFloat(faker.commerce().price(1, 10000000)))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(2, 7))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Create Fake Data Successful");
    }
}
