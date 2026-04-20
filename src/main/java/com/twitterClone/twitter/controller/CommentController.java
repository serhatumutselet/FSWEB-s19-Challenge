package com.twitterClone.twitter.controller;

import com.twitterClone.twitter.dto.CommentRequest;
import com.twitterClone.twitter.dto.CommentUpdateRequest;
import com.twitterClone.twitter.entity.Comment;
import com.twitterClone.twitter.service.CommentService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment create(@Valid @RequestBody CommentRequest request) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return commentService.createComment(username, request.getTweetId(), request.getContent());
    }

    @PutMapping("/{id}")
    public Comment update(@PathVariable Long id,
                           @Valid @RequestBody CommentUpdateRequest request) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return commentService.updateComment(id, username, request.getContent());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        commentService.deleteComment(id, username);
    }

    @GetMapping("/findByTweetId")
    public List<Comment> getByTweetId(@RequestParam Long tweetId) {
        return commentService.getCommentsByTweetId(tweetId);
    }
}