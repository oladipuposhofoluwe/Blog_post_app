package com.dee.blog_rest.services;

import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.security.CurrentUser;
import com.dee.blog_rest.security.UserPrincipal;

import java.util.List;

public interface FollowService {

    List<User> getUserFollowers(Long userId);
    List<User> getUserFollowing(Long userId);
    ApiResponse follow(Long idToFollow, UserPrincipal currentUser);
    ApiResponse unfollow(Long idToUnfollow, UserPrincipal currentUser);

}
