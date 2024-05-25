package com.example.blogs.Contollers;

import com.example.blogs.config.AppConstants;
import com.example.blogs.payloads.ApiResponse;
import com.example.blogs.payloads.PostDto;
import com.example.blogs.payloads.PostResponse;
import com.example.blogs.services.FileService;
import com.example.blogs.services.PostService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class PostController {

    private static final Logger logger = LogManager.getLogger(PostController.class);

    @Autowired
    private PostService postService;
    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;

    @PostMapping("/user/{userId}/category/{categoryId}/posts")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable Integer userId, @PathVariable Integer categoryId) {
        logger.info("Creating new post for user ID: {} and category ID: {}", userId, categoryId);
        PostDto createPost = this.postService.createPost(postDto, userId, categoryId);
        logger.info("Post created successfully with ID: {}", createPost.getPostId());
        return new ResponseEntity<>(createPost, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId) {
        logger.info("Fetching posts for user ID: {}", userId);
        List<PostDto> posts = this.postService.getPostByUser(userId);
        logger.info("Fetched {} posts for user ID: {}", posts.size(), userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}/posts")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Integer categoryId) {
        logger.info("Fetching posts for category ID: {}", categoryId);
        List<PostDto> posts = this.postService.getPostsByCategory(categoryId);
        logger.info("Fetched {} posts for category ID: {}", posts.size(), categoryId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/posts")
    public ResponseEntity<PostResponse> getAllPost(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {
        logger.info("Fetching all posts with pageNumber: {}, pageSize: {}, sortBy: {}, sortDir: {}", pageNumber, pageSize, sortBy, sortDir);
        PostResponse postResponse = this.postService.getAllPost(pageNumber, pageSize, sortBy, sortDir);
        logger.info("Fetched {} posts", postResponse.getContent().size());
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId) {
        logger.info("Fetching post with ID: {}", postId);
        PostDto postDto = this.postService.getPostById(postId);
        logger.info("Post fetched successfully with ID: {}", postDto.getPostId());
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}")
    public ApiResponse deletePost(@PathVariable Integer postId) {
        logger.info("Deleting post with ID: {}", postId);
        this.postService.deletePost(postId);
        logger.info("Post deleted successfully with ID: {}", postId);
        return new ApiResponse("Post is successfully Deleted", true);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, @PathVariable Integer postId) {
        logger.info("Updating post with ID: {}", postId);
        PostDto updatedPost = this.postService.updatePost(postDto, postId);
        logger.info("Post updated successfully with ID: {}", updatedPost.getPostId());
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @GetMapping("/posts/search/{keywords}")
    public ResponseEntity<List<PostDto>> searchByTitle(@PathVariable("keywords") String keywords) {
        logger.info("Searching posts with keywords: '{}'", keywords);
        List<PostDto> result = this.postService.searchPosts(keywords);
        logger.info("Found {} posts with keywords: '{}'", result.size(), keywords);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/post/image/upload/{postId}")
    public ResponseEntity<PostDto> uploadPostImage(@RequestParam("image") MultipartFile image, @PathVariable Integer postId) throws IOException {
        logger.info("Uploading image for post ID: {}", postId);
        PostDto postDto = this.postService.getPostById(postId);
        String fileName = this.fileService.uploadImage(path, image);
        postDto.setImageName(fileName);
        PostDto updatedPost = this.postService.updatePost(postDto, postId);
        logger.info("Image uploaded successfully for post ID: {}", postId);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @GetMapping(value = "/post/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void downloadImage(@PathVariable("imageName") String imageName, HttpServletResponse response) throws IOException {
        logger.info("Downloading image: {}", imageName);
        InputStream resource = this.fileService.getResource(path, imageName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
        logger.info("Image downloaded successfully: {}", imageName);
    }
}
