package com.example.blogs.services.implementations;

import com.example.blogs.Repositories.CommentRepository;
import com.example.blogs.Repositories.PostRepository;
import com.example.blogs.entities.Comment;
import com.example.blogs.exceptions.ResourceNotFoundException;
import com.example.blogs.payloads.CommentDto;
import com.example.blogs.services.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.blogs.entities.Post;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CommentImplementation implements CommentService {

    private static final Logger logger = LogManager.getLogger(CommentImplementation.class);

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CommentDto createComment(CommentDto commentDto, Integer postId) {
        logger.info("Creating new comment for post ID: {}", postId);
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        Comment comment = this.modelMapper.map(commentDto, Comment.class);
        comment.setPost(post);
        Comment savedComment = this.commentRepo.save(comment);
        logger.info("Comment created successfully with ID: {}", savedComment.getId());
        return this.modelMapper.map(savedComment, CommentDto.class);
    }

    @Override
    public void deleteComment(Integer commentId) {
        logger.info("Deleting comment with ID: {}", commentId);
        Comment com = this.commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "Comment Id", commentId));
        this.commentRepo.delete(com);
        logger.info("Comment deleted successfully with ID: {}", commentId);
    }
}
