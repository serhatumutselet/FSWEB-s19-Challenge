package com.twitterClone.twitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long tweetId;

    @NotBlank
    @Size(max = 280)
    private String content;
}