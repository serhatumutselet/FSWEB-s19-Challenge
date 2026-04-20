package com.twitterClone.twitter.controller;

import com.twitterClone.twitter.service.RetweetService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/retweet")
public class RetweetController {

    private final RetweetService retweetService;

    public RetweetController(RetweetService retweetService) {
        this.retweetService = retweetService;
    }

    @PostMapping
    public void retweet(@RequestParam Long tweetId) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        retweetService.retweet(username, tweetId);
    }

    @DeleteMapping("/{tweetId}")
    public void deleteRetweet(@PathVariable Long tweetId) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        retweetService.deleteRetweet(username, tweetId);
    }

    @GetMapping("/count")
    public long count(@RequestParam Long tweetId) {
        return retweetService.countByTweetId(tweetId);
    }

    @GetMapping("/isRetweeted")
    public boolean isRetweeted(@RequestParam Long tweetId) {
        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return retweetService.isRetweeted(username, tweetId);
    }
}