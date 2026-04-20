package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.Retweet;
import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.RetweetRepository;
import com.twitterClone.twitter.repository.TweetRepository;
import com.twitterClone.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RetweetService {

    private final RetweetRepository retweetRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;

    public RetweetService(RetweetRepository retweetRepository,
                          UserRepository userRepository,
                          TweetRepository tweetRepository) {
        this.retweetRepository = retweetRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
    }

    public void retweet(Long userId, Long tweetId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setTweet(tweet);

        retweetRepository.save(retweet);
    }

    public void retweet(String username, Long tweetId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

    
        if (retweetRepository.existsByUserIdAndTweetId(user.getId(), tweetId)) {
           
            return;
        }

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setTweet(tweet);

        retweetRepository.save(retweet);
    }

    public void deleteRetweet(String username, Long tweetId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        
        retweetRepository.deleteByUserIdAndTweetId(user.getId(), tweetId);
    }

    public boolean isRetweeted(String username, Long tweetId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return retweetRepository.existsByUserIdAndTweetId(user.getId(), tweetId);
    }

    public long countByTweetId(Long tweetId) {
        return retweetRepository.countByTweetId(tweetId);
    }

    public java.util.List<Tweet> getTweetsRetweetedByUser(Long userId) {
        
        java.util.List<Retweet> retweets = retweetRepository.findByUserId(userId);
        return retweets.stream().map(Retweet::getTweet).toList();
    }
}