package com.dee.blog_rest.requests_and_responses;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentRequest {
    @NotBlank
    @Size(min = 10)
    private String body;
}
