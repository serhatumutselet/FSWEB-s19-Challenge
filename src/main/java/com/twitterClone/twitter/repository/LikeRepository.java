package com.twitterClone.twitter.repository;

import com.twitterClone.twitter.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    @Modifying
    @Query(value = "delete from likes where user_id = :userId and tweet_id = :tweetId", nativeQuery = true)
    void deleteByUserIdAndTweetId(@Param("userId") Long userId, @Param("tweetId") Long tweetId);

    long countByTweetId(Long tweetId);

    @Modifying
    @Query(value = "delete from likes where tweet_id = :tweetId", nativeQuery = true)
    void deleteByTweetId(@Param("tweetId") Long tweetId);
}