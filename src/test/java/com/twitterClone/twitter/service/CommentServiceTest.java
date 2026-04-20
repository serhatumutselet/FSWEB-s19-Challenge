package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.Comment;
import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.CommentRepository;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void updateComment_throwsUnauthorized_whenCurrentUserIsNotCommentOwner() {
        Comment comment = new Comment();
        comment.setId(1L);

        User commentOwner = new User();
        commentOwner.setId(2L);
        comment.setUser(commentOwner);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        User currentUser = new User();
        currentUser.setId(3L);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(currentUser));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> commentService.updateComment(1L, "user1", "new content")
        );
        assertEquals("You can only update your own comment", ex.getMessage());
    }

    @Test
    void deleteComment_allowsTweetOwner() {
        Comment comment = new Comment();
        comment.setId(1L);

        User commentOwner = new User();
        commentOwner.setId(2L);
        comment.setUser(commentOwner);

        Tweet tweet = new Tweet();
        User tweetOwner = new User();
        tweetOwner.setId(3L);
        tweetOwner.setUsername("tweetOwner");
        tweet.setUser(tweetOwner);
        comment.setTweet(tweet);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        User currentUser = new User();
        currentUser.setId(3L);
        currentUser.setUsername("tweetOwner");
        when(userRepository.findByUsername("tweetOwner")).thenReturn(Optional.of(currentUser));

        commentService.deleteComment(1L, "tweetOwner");

        verify(commentRepository).delete(comment);
    }
}

