package com.twitterClone.twitter.controller;

import com.twitterClone.twitter.dto.TweetRequest;
import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.service.TweetService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tweet")
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public Tweet createTweet(@Valid @RequestBody TweetRequest request) {
        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return tweetService.createTweet(username, request.getContent());
    }
    @GetMapping("/findByUserId")
    public List<Tweet> getByUserId(@RequestParam Long userId) {
        return tweetService.getTweetsByUserId(userId);
    }

    @GetMapping("/ownFeed")
    public List<Tweet> ownFeed() {
        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return tweetService.getTweetsForCurrentUser(username);
    }

    @GetMapping("/findById")
    public Tweet getById(@RequestParam Long id) {
        return tweetService.getTweetById(id);
    }

    @GetMapping("/findAll")
    public List<Tweet> findAll() {
        return tweetService.getAllTweets();
    }
    @PutMapping("/{id}")
    public Tweet updateTweet(@PathVariable Long id,
                             @Valid @RequestBody TweetRequest request) {
        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return tweetService.updateTweet(id, username, request.getContent());
    }
    @DeleteMapping("/{id}")
    public void deleteTweet(@PathVariable Long id) {
        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        tweetService.deleteTweet(id, username);
    }

}