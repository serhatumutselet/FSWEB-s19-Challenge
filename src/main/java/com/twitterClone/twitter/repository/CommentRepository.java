package com.twitterClone.twitter.repository;

import com.twitterClone.twitter.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTweetId(Long tweetId);

    @Modifying
    @Query(value = "delete from comment where tweet_id = :tweetId", nativeQuery = true)
    void deleteByTweetId(@Param("tweetId") Long tweetId);
}