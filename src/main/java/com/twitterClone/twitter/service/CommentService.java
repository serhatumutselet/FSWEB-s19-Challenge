package com.twitterClone.twitter.service;

import com.twitterClone.twitter.entity.Comment;
import com.twitterClone.twitter.entity.Tweet;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.CommentRepository;
import com.twitterClone.twitter.repository.TweetRepository;
import com.twitterClone.twitter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          TweetRepository tweetRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
    }

    public Comment createComment(Long userId, Long tweetId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTweet(tweet);
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    public Comment createComment(String username, Long tweetId, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTweet(tweet);
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, String username, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only comment owner can update
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own comment");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long commentOwnerId = comment.getUser().getId();
        Long tweetOwnerId = comment.getTweet().getUser().getId();

       
        if (!currentUser.getId().equals(commentOwnerId) && !currentUser.getId().equals(tweetOwnerId)) {
            throw new RuntimeException("You can only delete your own comment");
        }

        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByTweetId(Long tweetId) {
        return commentRepository.findByTweetId(tweetId);
    }
}