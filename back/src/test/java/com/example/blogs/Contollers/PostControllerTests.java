package com.example.blogs.Contollers;

import com.example.blogs.Contollers.PostController;
import com.example.blogs.entities.Post;
import com.example.blogs.payloads.ApiResponse;
import com.example.blogs.payloads.PostDto;
import com.example.blogs.payloads.PostResponse;
import com.example.blogs.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTests {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    public void testCreatePost() {
        // Mocking service response
        PostDto postDto = new PostDto();
        when(postService.createPost(any(PostDto.class), any(Integer.class), any(Integer.class))).thenReturn(postDto);

        // Calling controller method
        ResponseEntity<PostDto> responseEntity = postController.createPost(postDto, 1, 1);

        // Assertions
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(postDto, responseEntity.getBody());
    }

    @Test
    public void testUpdatePost() {
        // Mocking service response
        PostDto postDto = new PostDto();
        when(postService.updatePost(any(PostDto.class), any(Integer.class))).thenReturn(postDto);

        // Calling controller method
        ResponseEntity<PostDto> responseEntity = postController.updatePost(postDto, 1);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(postDto, responseEntity.getBody());
    }

    @Test
    public void testDeletePost() {
        // Calling controller method
        ApiResponse response = postController.deletePost(1);

        // Verify that postService.deletePost method was called with the correct parameter
        verify(postService, times(1)).deletePost(1);
    }

    @Test
    public void testGetPostsByCategory() {
        // Mocking service response
        List<PostDto> posts = new ArrayList<>();
        when(postService.getPostsByCategory(any(Integer.class))).thenReturn(posts);

        // Calling controller method
        ResponseEntity<List<PostDto>> responseEntity = postController.getPostsByCategory(1);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(posts, responseEntity.getBody());
    }

    @Test
    public void testGetPostsByUser() {
        // Mocking service response
        List<PostDto> posts = new ArrayList<>();
        when(postService.getPostByUser(any(Integer.class))).thenReturn(posts);

        // Calling controller method
        ResponseEntity<List<PostDto>> responseEntity = postController.getPostsByUser(1);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(posts, responseEntity.getBody());
    }

    @Test
    public void testSearchPosts() {
        // Mocking service response
        List<PostDto> posts = new ArrayList<>();
        when(postService.searchPosts(any(String.class))).thenReturn(posts);

        // Calling controller method
        ResponseEntity<List<PostDto>> responseEntity = postController.searchByTitle("keyword");

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(posts, responseEntity.getBody());
    }
}
