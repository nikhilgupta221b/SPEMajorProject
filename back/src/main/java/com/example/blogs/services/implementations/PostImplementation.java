package com.example.blogs.services.implementations;

import com.example.blogs.Repositories.CategoryRepository;
import com.example.blogs.Repositories.PostRepository;
import com.example.blogs.Repositories.UserRepository;
import com.example.blogs.entities.Category;
import com.example.blogs.entities.Post;
import com.example.blogs.entities.User;
import com.example.blogs.exceptions.ResourceNotFoundException;
import com.example.blogs.payloads.PostDto;
import com.example.blogs.payloads.PostResponse;
import com.example.blogs.services.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostImplementation implements PostService {

    private static final Logger logger = LogManager.getLogger(PostImplementation.class);

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public PostDto createPost(PostDto postDto, Integer userId, Integer categoryId) {
        logger.info("Creating new post for user ID: {} and category ID: {}", userId, categoryId);
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));
        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        Post post = this.modelMapper.map(postDto, Post.class);
        post.setImageName("default.png");
        post.setAddDate(new Date());
        post.setUser(user);
        post.setCategory(category);

        Post newPost = this.postRepo.save(post);
        PostDto responseDto = this.modelMapper.map(newPost, PostDto.class);
        responseDto.setAddDate(newPost.getAddDate());
        logger.info("Post created successfully with ID: {}", newPost.getPostId());
        return responseDto;
    }

    @Override
    public PostDto updatePost(PostDto postDto, Integer postId) {
        logger.info("Updating post with ID: {}", postId);
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));

        Category category = this.categoryRepo.findById(postDto.getCategory().getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", postDto.getCategory().getCategoryId()));
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImageName(postDto.getImageName());
        post.setCategory(category);

        Post updatedPost = this.postRepo.save(post);
        logger.info("Post updated successfully with ID: {}", updatedPost.getPostId());
        return this.modelMapper.map(updatedPost, PostDto.class);
    }

    @Override
    public void deletePost(Integer postId) {
        logger.info("Deleting post with ID: {}", postId);
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        this.postRepo.delete(post);
        logger.info("Post deleted successfully with ID: {}", postId);
    }

    @Override
    public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all posts with pageNumber: {}, pageSize: {}, sortBy: {}, sortDir: {}", pageNumber, pageSize, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Post> pagePost = this.postRepo.findAll(pageable);
        List<Post> allPosts = pagePost.getContent();
        List<PostDto> postDtos = allPosts.stream()
                .map(post -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(postDtos);
        postResponse.setPageNumber(pagePost.getNumber());
        postResponse.setPageSize(pagePost.getSize());
        postResponse.setTotalElements(pagePost.getTotalElements());
        postResponse.setTotalPages(pagePost.getTotalPages());
        postResponse.setLastPage(pagePost.isLast());
        logger.info("Fetched {} posts", postResponse.getContent().size());
        return postResponse;
    }

    @Override
    public PostDto getPostById(Integer postId) {
        logger.info("Fetching post with ID: {}", postId);
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        logger.info("Post fetched successfully with ID: {}", post.getPostId());
        return this.modelMapper.map(post, PostDto.class);
    }

    @Override
    public List<PostDto> getPostsByCategory(Integer categoryId) {
        logger.info("Fetching posts for category ID: {}", categoryId);
        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        List<Post> posts = this.postRepo.findByCategory(category);
        List<PostDto> postDtos = posts.stream()
                .map(post -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
        logger.info("Fetched {} posts for category ID: {}", postDtos.size(), categoryId);
        return postDtos;
    }

    @Override
    public List<PostDto> getPostByUser(Integer userId) {
        logger.info("Fetching posts for user ID: {}", userId);
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));
        List<Post> posts = this.postRepo.findByUser(user);
        List<PostDto> postDtos = posts.stream()
                .map(post -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
        logger.info("Fetched {} posts for user ID: {}", postDtos.size(), userId);
        return postDtos;
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        logger.info("Searching posts with keyword: {}", keyword);
        List<Post> posts = this.postRepo.findByTitleContaining(keyword);
        List<PostDto> postDtos = posts.stream()
                .map(post -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
        logger.info("Found {} posts with keyword: {}", postDtos.size(), keyword);
        return postDtos;
    }
}
