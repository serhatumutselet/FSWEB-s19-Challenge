package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.LikeEntity;
import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.LikeRepository;
import com.twitterClone.twitter.repository.TweetRepository;
import com.twitterClone.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;

    public LikeService(LikeRepository likeRepository,
                       UserRepository userRepository,
                       TweetRepository tweetRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
    }

    public void like(Long userId, Long tweetId) {

        if (likeRepository.existsByUserIdAndTweetId(userId, tweetId)) {
            
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        LikeEntity like = new LikeEntity();
        like.setUser(user);
        like.setTweet(tweet);

        likeRepository.save(like);
    }

    public void like(String username, Long tweetId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        like(user.getId(), tweetId);
    }

    public void dislike(Long userId, Long tweetId) {
        likeRepository.deleteByUserIdAndTweetId(userId, tweetId);
    }

    public void dislike(String username, Long tweetId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        dislike(user.getId(), tweetId);
    }

    public boolean isLiked(String username, Long tweetId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return likeRepository.existsByUserIdAndTweetId(user.getId(), tweetId);
    }

    public long countByTweetId(Long tweetId) {
        return likeRepository.countByTweetId(tweetId);
    }
}