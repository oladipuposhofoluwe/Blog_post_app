package com.dee.blog_rest.controller;

import com.dee.blog_rest.entities.Post;
import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.requests_and_responses.PagedResponse;
import com.dee.blog_rest.requests_and_responses.PostRequest;
import com.dee.blog_rest.requests_and_responses.PostResponse;
import com.dee.blog_rest.security.CurrentUser;
import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.services.PostService;
import com.dee.blog_rest.services.serviceImplementation.PostLikeService;
import com.dee.blog_rest.services.serviceImplementation.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    PostLikeService postLikeService;

    @Autowired
    private PostService postService;

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<Post>> getAllPosts(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "30") Integer size) {

        PagedResponse<Post> response = postService.getAllPosts(page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponse> addPost(@Valid @RequestBody PostRequest postRequest,
                                                @CurrentUser UserPrincipal currentUser) {

        PostResponse postResponse = postService.addPost(postRequest, currentUser);

        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }


    @PostMapping("/{id}/favorites")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> addOrRemoveFavourite(@PathVariable(name = "id") Long id,
                                                      @CurrentUser UserPrincipal currentUser) {

        return postService.addToFavorites(id, currentUser);
        }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable(name = "id") Long id) {
        Post post = postService.getPost(id);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable(name = "id") Long id,
                                           @Valid @RequestBody PostRequest newPostRequest,
                                           @CurrentUser UserPrincipal currentUser) {
        Post post = postService.updatePost(id, newPostRequest, currentUser);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable(name = "id") Long id,
                                                  @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = postService.deletePost(id, currentUser);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/{post_id}/like")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> likePost(@PathVariable(name = "post_id") Long postId,
                                                @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = postLikeService.likePost(postId, currentUser);
        return ResponseEntity.ok(apiResponse);
    }

}
