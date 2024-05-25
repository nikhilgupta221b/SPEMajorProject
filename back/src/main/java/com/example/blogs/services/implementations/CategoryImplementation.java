package com.example.blogs.services.implementations;

import com.example.blogs.Repositories.CategoryRepository;
import com.example.blogs.entities.Category;
import com.example.blogs.exceptions.ResourceNotFoundException;
import com.example.blogs.services.CategoryService;
import com.example.blogs.payloads.CategoryDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryImplementation implements CategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryImplementation.class);

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        logger.info("Creating new category with title: {}", categoryDto.getCategoryTitle());
        Category cat = this.modelMapper.map(categoryDto, Category.class);
        Category addedCat = this.categoryRepo.save(cat);
        logger.info("Category created successfully with ID: {}", addedCat.getCategoryId());
        return this.modelMapper.map(addedCat, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId) {
        logger.info("Updating category with ID: {}", categoryId);
        Category cat = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        cat.setCategoryTitle(categoryDto.getCategoryTitle());
        cat.setCategoryDescription(categoryDto.getCategoryDescription());
        Category updatedCat = this.categoryRepo.save(cat);
        logger.info("Category updated successfully with ID: {}", updatedCat.getCategoryId());
        return this.modelMapper.map(updatedCat, CategoryDto.class);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        logger.info("Deleting category with ID: {}", categoryId);
        Category cat = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        this.categoryRepo.delete(cat);
        logger.info("Category deleted successfully with ID: {}", categoryId);
    }

    @Override
    public CategoryDto getCategory(Integer categoryId) {
        logger.info("Fetching category with ID: {}", categoryId);
        Category cat = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        logger.info("Category fetched successfully with ID: {}", cat.getCategoryId());
        return this.modelMapper.map(cat, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> getCategories() {
        logger.info("Fetching all categories");
        List<Category> categories = this.categoryRepo.findAll();
        List<CategoryDto> catDtos = categories.stream()
                .map(cat -> this.modelMapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());
        logger.info("Fetched {} categories", catDtos.size());
        return catDtos;
    }
}
