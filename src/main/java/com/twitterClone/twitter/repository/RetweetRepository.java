package com.twitterClone.twitter.repository;

import com.twitterClone.twitter.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    @Modifying
    @Query(value = "delete from retweets where user_id = :userId and tweet_id = :tweetId", nativeQuery = true)
    void deleteByUserIdAndTweetId(@Param("userId") Long userId, @Param("tweetId") Long tweetId);

    long countByTweetId(Long tweetId);

    java.util.List<Retweet> findByUserId(Long userId);

    @Modifying
    @Query(value = "delete from retweets where tweet_id = :tweetId", nativeQuery = true)
    void deleteByTweetId(@Param("tweetId") Long tweetId);

    
    @Modifying
    @Query(value = "delete from retweet where tweet_id = :tweetId", nativeQuery = true)
    void deleteByTweetIdLegacy(@Param("tweetId") Long tweetId);
}