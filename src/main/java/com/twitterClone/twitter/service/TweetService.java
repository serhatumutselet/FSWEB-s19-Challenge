package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.entity.Retweet;
import com.twitterClone.twitter.repository.CommentRepository;
import com.twitterClone.twitter.repository.LikeRepository;
import com.twitterClone.twitter.repository.TweetRepository;
import com.twitterClone.twitter.repository.UserRepository;
import com.twitterClone.twitter.repository.RetweetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final RetweetRepository retweetRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public TweetService(TweetRepository tweetRepository,
                        UserRepository userRepository,
                        RetweetRepository retweetRepository,
                        CommentRepository commentRepository,
                        LikeRepository likeRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.retweetRepository = retweetRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    public Tweet createTweet(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = new Tweet();
        tweet.setContent(content);
        tweet.setUser(user);

        return tweetRepository.save(tweet);
    }

    public Tweet createTweet(String username, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = new Tweet();
        tweet.setContent(content);
        tweet.setUser(user);

        return tweetRepository.save(tweet);
    }
    public List<Tweet> getTweetsByUserId(Long userId) {
        return tweetRepository.findByUserId(userId);
    }

    public List<Tweet> getTweetsForCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getOwnFeedByUserId(user.getId());
    }

    public List<Tweet> getOwnFeedByUserId(Long userId) {
        List<Tweet> ownTweets = tweetRepository.findByUserId(userId);
        List<Tweet> retweetedTweets = retweetRepository.findByUserId(userId).stream()
                .map(Retweet::getTweet)
                .toList();

        Map<Long, Tweet> byId = new LinkedHashMap<>();
        for (Tweet t : ownTweets) {
            if (t != null) byId.put(t.getId(), t);
        }
        for (Tweet t : retweetedTweets) {
            if (t != null) byId.putIfAbsent(t.getId(), t);
        }
        return byId.values().stream().toList();
    }

    public List<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    public Tweet getTweetById(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));
    }
    public Tweet updateTweet(Long tweetId, Long userId, String content) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        // AUTHORIZATION
        if (!tweet.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own tweet");
        }

        tweet.setContent(content);
        return tweetRepository.save(tweet);
    }

    public Tweet updateTweet(Long tweetId, String username, String content) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // AUTHORIZATION: only owner can update
        if (!tweet.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own tweet");
        }

        tweet.setContent(content);
        return tweetRepository.save(tweet);
    }

    @Transactional
    public void deleteTweet(Long tweetId, Long userId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        //  AUTHORIZATION
        if (!tweet.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own tweet");
        }

        // Remove children first to satisfy FK constraints.
        commentRepository.deleteByTweetId(tweetId);
        likeRepository.deleteByTweetId(tweetId);
        retweetRepository.deleteByTweetId(tweetId);
        retweetRepository.deleteByTweetIdLegacy(tweetId);
        tweetRepository.delete(tweet);
    }

    @Transactional
    public void deleteTweet(Long tweetId, String username) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // AUTHORIZATION: only owner can delete
        if (!tweet.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own tweet");
        }

        commentRepository.deleteByTweetId(tweetId);
        likeRepository.deleteByTweetId(tweetId);
        retweetRepository.deleteByTweetId(tweetId);
        retweetRepository.deleteByTweetIdLegacy(tweetId);
        tweetRepository.delete(tweet);
    }
}