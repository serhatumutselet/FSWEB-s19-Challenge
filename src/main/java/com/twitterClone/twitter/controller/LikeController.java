package com.twitterClone.twitter.controller;

import com.twitterClone.twitter.service.LikeService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public void like(@RequestParam Long tweetId) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        likeService.like(username, tweetId);
    }

    @PostMapping("/dislike")
    public void dislike(@RequestParam Long tweetId) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        likeService.dislike(username, tweetId);
    }

    @GetMapping("/count")
    public long count(@RequestParam Long tweetId) {
        return likeService.countByTweetId(tweetId);
    }

    @GetMapping("/isLiked")
    public boolean isLiked(@RequestParam Long tweetId) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return likeService.isLiked(username, tweetId);
    }
}