package com.dee.blog_rest.requests_and_responses;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PostResponse {
	private String title;
	private String body;
	private  String status;

}
