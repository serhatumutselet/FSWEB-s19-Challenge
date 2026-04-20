package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.LikeEntity;
import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.LikeRepository;
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
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void like_throwsAlreadyLiked_whenLikeExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(likeRepository.existsByUserIdAndTweetId(1L, 10L)).thenReturn(true);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> likeService.like("user1", 10L)
        );
        assertEquals("Already liked", ex.getMessage());
    }

    @Test
    void dislike_deletesLike_whenExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        likeService.dislike("user1", 10L);

        verify(likeRepository).deleteByUserIdAndTweetId(1L, 10L);
    }
}

