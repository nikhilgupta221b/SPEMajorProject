package com.example.blogs.Contollers;

import com.example.blogs.payloads.ApiResponse;
import com.example.blogs.payloads.CommentDto;
import com.example.blogs.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/v1/")
public class CommentController {

    private static final Logger logger = LogManager.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @PostMapping("/post/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto comment, @PathVariable Integer postId){
        logger.info("Creating new comment for post ID: {}", postId);
        CommentDto createComment = this.commentService.createComment(comment, postId);
        logger.info("Comment created successfully with ID: {}", createComment.getId());
        return new ResponseEntity<>(createComment, HttpStatus.CREATED);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Integer commentId){
        logger.info("Deleting comment with ID: {}", commentId);
        this.commentService.deleteComment(commentId);
        logger.info("Comment deleted successfully with ID: {}", commentId);
        return new ResponseEntity<>(new ApiResponse("Comment Deleted Successfully", true), HttpStatus.OK);
    }
}
