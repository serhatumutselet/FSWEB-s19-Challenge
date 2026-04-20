package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.CommentRepository;
import com.twitterClone.twitter.repository.LikeRepository;
import com.twitterClone.twitter.repository.TweetRepository;
import com.twitterClone.twitter.repository.RetweetRepository;
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
class TweetServiceTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RetweetRepository retweetRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private TweetService tweetService;

    @Test
    void updateTweet_throwsUnauthorized_whenCurrentUserIsNotOwner() {
        Tweet tweet = new Tweet();
        tweet.setId(10L);

        User tweetOwner = new User();
        tweetOwner.setId(2L);
        tweet.setUser(tweetOwner);

        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));

        User currentUser = new User();
        currentUser.setId(1L);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(currentUser));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> tweetService.updateTweet(10L, "user1", "new content")
        );
        assertEquals("You can only update your own tweet", ex.getMessage());
    }

    @Test
    void updateTweet_saves_whenOwner() {
        Tweet tweet = new Tweet();
        tweet.setId(10L);

        User tweetOwner = new User();
        tweetOwner.setId(1L);
        tweet.setUser(tweetOwner);

        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));

        User currentUser = new User();
        currentUser.setId(1L);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(currentUser));

        when(tweetRepository.save(any(Tweet.class))).thenAnswer(inv -> inv.getArgument(0));

        Tweet updated = tweetService.updateTweet(10L, "user1", "updated");

        assertEquals("updated", updated.getContent());
        verify(tweetRepository).save(any(Tweet.class));
    }

    @Test
    void deleteTweet_throwsUnauthorized_whenCurrentUserIsNotOwner() {
        Tweet tweet = new Tweet();
        tweet.setId(10L);

        User tweetOwner = new User();
        tweetOwner.setId(2L);
        tweet.setUser(tweetOwner);

        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));

        User currentUser = new User();
        currentUser.setId(1L);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(currentUser));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> tweetService.deleteTweet(10L, "user1")
        );
        assertEquals("You can only delete your own tweet", ex.getMessage());
    }
}

