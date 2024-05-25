package com.example.blogs.Contollers;

import com.example.blogs.payloads.ApiResponse;
import com.example.blogs.payloads.CategoryDto;
import com.example.blogs.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private static final Logger logger = LogManager.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto){
        logger.info("Creating new category with title: {}", categoryDto.getCategoryTitle());
        CategoryDto createCategory = this.categoryService.createCategory(categoryDto);
        logger.info("Category created successfully with ID: {}", createCategory.getCategoryId());
        return new ResponseEntity<>(createCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Integer categoryId){
        logger.info("Updating category with ID: {}", categoryId);
        CategoryDto updatedCategory = this.categoryService.updateCategory(categoryDto, categoryId);
        logger.info("Category updated successfully with ID: {}", updatedCategory.getCategoryId());
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer categoryId){
        logger.info("Deleting category with ID: {}", categoryId);
        this.categoryService.deleteCategory(categoryId);
        logger.info("Category deleted successfully with ID: {}", categoryId);
        return new ResponseEntity<>(new ApiResponse("Category is deleted Successfully", true), HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Integer categoryId){
        logger.info("Fetching category with ID: {}", categoryId);
        CategoryDto categoryDto = this.categoryService.getCategory(categoryId);
        logger.info("Category fetched successfully with ID: {}", categoryDto.getCategoryId());
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<CategoryDto>> getCategories(){
        logger.info("Fetching all categories");
        List<CategoryDto> categories = this.categoryService.getCategories();
        logger.info("Fetched {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }
}
