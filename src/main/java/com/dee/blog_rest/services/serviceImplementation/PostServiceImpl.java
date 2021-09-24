package com.dee.blog_rest.services.serviceImplementation;

import com.dee.blog_rest.exceptions.AppException;
import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.entities.Post;
import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.entities.role.RoleName;
import com.dee.blog_rest.exceptions.BadRequestException;
import com.dee.blog_rest.exceptions.UnauthorizedException;
import com.dee.blog_rest.repositories.PostRepository;
import com.dee.blog_rest.repositories.UserRepository;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.requests_and_responses.PagedResponse;
import com.dee.blog_rest.requests_and_responses.PostRequest;
import com.dee.blog_rest.requests_and_responses.PostResponse;
import com.dee.blog_rest.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class PostServiceImpl implements PostService {
	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserServiceImplementation userServiceImplementation;

	@Autowired
	private PostService postService;

	@Override
	public PagedResponse<Post> getAllPosts(int page, int size) {
		validatePageNumberAndSize(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

		Page<Post> posts = postRepository.findAll(pageable);

		List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

		return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
				posts.getTotalPages(), posts.isLast());
	}

	@Override
	public ResponseEntity<ApiResponse> addToFavorites(Long id, UserPrincipal currentUser) {
		return null;
	}

	@Override
	public PagedResponse<Post> getPostsByCreatedBy(String email, int page, int size) {
		validatePageNumberAndSize(page, size);
		User user = userRepository.findByEmail(email);
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		Page<Post> posts = postRepository.findByCreatedBy(user.getId(), pageable);

		List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

		return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
				posts.getTotalPages(), posts.isLast());
	}


	@Override
	public Post updatePost(Long id, PostRequest newPostRequest, UserPrincipal currentUser) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalStateException("Resource not found"));

		if (post.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			post.setCaption(newPostRequest.getTitle());
			post.setBody(newPostRequest.getBody());
			post.setUpdatedAt(Instant.now());
			return postRepository.save(post);
		}
		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to edit this post");

		throw new UnauthorizedException(apiResponse);
	}

	@Override
	public ApiResponse deletePost(Long id, UserPrincipal currentUser) {
		Post post = postRepository.findById(id).orElseThrow(() -> new IllegalStateException("Resource not found"));
		if (post.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			postRepository.deleteById(id);
			return new ApiResponse(Boolean.TRUE, "You successfully deleted post");
		}

		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this post");

		throw new UnauthorizedException(apiResponse);
	}

	@Override
	public PostResponse addPost(PostRequest postRequest, UserPrincipal currentUser) {
		User user = userRepository.findById(currentUser.getId())
				.orElseThrow(() -> new AppException("User not found"));

		Post post = new Post();
		post.setBody(postRequest.getBody());
		post.setCaption(postRequest.getTitle());
		post.setCreatedAt(Instant.now());
		post.setUpdatedAt(Instant.now());
		post.setCreatedBy(user.getId().toString());

		post.setUser(user);

		Post newPost = postRepository.save(post);

		PostResponse postResponse = new PostResponse();

		postResponse.setTitle(newPost.getCaption());
		postResponse.setBody(newPost.getBody());
		postResponse.setStatus("POST CREATED!!!");

		return postResponse;
	}

	@Override
	public Post getPost(Long id) {
		return postRepository.findById(id).orElseThrow(() -> new IllegalStateException("Resource not found"));
	}

//	@Override
//	@Transactional
//	public ResponseEntity<ApiResponse> addToFavorites(Long id, UserPrincipal currentUser){
//
//		Post post = postService.getPost(id);
//
//		if (post != null) {
//
//			User userbyId = userServiceImplementation.findById(currentUser.getId());
//			List<Post> favorites = userbyId.getFavorites();
//
//			Optional<Post> optionalPost = favorites.stream().filter(post1 -> post.equals(post1)).findFirst();
//
//			if(optionalPost.isPresent()) {
//				favorites.remove(post);
//				post.setUser(userbyId);
//				return ResponseEntity.ok(new ApiResponse(Boolean.TRUE, "Post REMOVED FROM favourites successfully"));
//			}
//
//			favorites.add(post);
//			post.setUser(userbyId);
//			return ResponseEntity.ok(new ApiResponse(Boolean.TRUE, "Post ADDED TO favourites successfully"));
//		}
//		return ResponseEntity.ok(new ApiResponse(Boolean.FALSE, "Could not add post to favourite"));
//
//	}


	private void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BadRequestException("Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new BadRequestException("Size number cannot be less than zero.");
		}

		if (size > 30) {
			throw new BadRequestException("Page size must not be greater than " + 30);
		}
	}
}
