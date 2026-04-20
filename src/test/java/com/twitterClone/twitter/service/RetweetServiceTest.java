package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.Retweet;
import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.RetweetRepository;
import com.twitterClone.twitter.repository.TweetRepository;
import com.twitterClone.twitter.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetweetServiceTest {

    @Mock
    private RetweetRepository retweetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private RetweetService retweetService;

    @Test
    void retweet_throwsAlreadyRetweeted_whenRetweetExists() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(retweetRepository.existsByUserIdAndTweetId(1L, 10L)).thenReturn(true);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> retweetService.retweet("user1", 10L)
        );
        assertEquals("Already retweeted", ex.getMessage());
    }

    @Test
    void deleteRetweet_callsRepositoryDelete() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        retweetService.deleteRetweet("user1", 10L);

        verify(retweetRepository).deleteByUserIdAndTweetId(1L, 10L);
    }
}

