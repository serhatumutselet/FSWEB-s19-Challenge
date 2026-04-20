package com.twitterClone.twitter.repository;

import com.twitterClone.twitter.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByUserId(Long userId);
}