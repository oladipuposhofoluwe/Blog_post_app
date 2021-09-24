package com.dee.blog_rest.services;

import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.entities.Post;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.requests_and_responses.PagedResponse;
import com.dee.blog_rest.requests_and_responses.PostRequest;
import com.dee.blog_rest.requests_and_responses.PostResponse;
import org.springframework.http.ResponseEntity;

public interface PostService {

	PagedResponse<Post> getAllPosts(int page, int size);

	ResponseEntity<ApiResponse> addToFavorites(Long id, UserPrincipal currentUser);

	PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size);
	Post updatePost(Long id, PostRequest newPostRequest, UserPrincipal currentUser);

	ApiResponse deletePost(Long id, UserPrincipal currentUser);

	PostResponse addPost(PostRequest postRequest, UserPrincipal currentUser);

	Post getPost(Long id);

}
