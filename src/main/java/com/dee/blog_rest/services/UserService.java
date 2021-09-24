package com.dee.blog_rest.services;

import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.requests_and_responses.AddUserRequest;
import com.dee.blog_rest.requests_and_responses.SignUpRequest;
import com.dee.blog_rest.requests_and_responses.UpdateUserRequest;
import com.dee.blog_rest.security.UserPrincipal;

import java.util.List;


public interface UserService {
    User addUser(AddUserRequest user);
    List<User> findAll();
    User updateUser(UpdateUserRequest newUser, Long id, UserPrincipal currentUser);

}
