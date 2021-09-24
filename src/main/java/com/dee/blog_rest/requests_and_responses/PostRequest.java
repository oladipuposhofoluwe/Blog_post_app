package com.dee.blog_rest.requests_and_responses;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PostRequest {

	@NotBlank
	@Size(min = 5)
	private String title;

	@NotBlank
	@Size(min = 10)
	private String body;

}
