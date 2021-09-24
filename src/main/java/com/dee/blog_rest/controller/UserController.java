package com.dee.blog_rest.controller;

import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.requests_and_responses.AddUserRequest;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.requests_and_responses.UpdateUserRequest;
import com.dee.blog_rest.requests_and_responses.UserDetailsResponse;
import com.dee.blog_rest.security.CurrentUser;
import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.services.serviceImplementation.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private UserServiceImplementation userServiceImplementation;

    @Autowired
    public UserController(UserServiceImplementation userServiceImplementation) {
        this.userServiceImplementation = userServiceImplementation;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDetailsResponse> allUsers(){
        List<User> all = userServiceImplementation.findAll();

        List<UserDetailsResponse> users = new ArrayList<>();
        all.forEach(user -> {
            UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
            userDetailsResponse.setId(user.getId());
            userDetailsResponse.setEmail(user.getEmail());
            userDetailsResponse.setDateCreated(user.getCreatedAt().toString());
            userDetailsResponse.setFirstName(user.getFirstName());
            userDetailsResponse.setLastName(user.getLastName());
//            userDetailsResponse.setTotalNumberOfConnections(user.getConnections().size());
            userDetailsResponse.setTotalNumberOfPosts(user.getPosts().size());
            users.add(userDetailsResponse);
        });
        return users;
    }

    @PutMapping(path = "{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable("userId") Long userId,
                                                  @Valid @RequestBody UpdateUserRequest updateUser,
                                                  @CurrentUser UserPrincipal currentUser){


            userServiceImplementation.updateUser(updateUser, userId, currentUser);
            return ResponseEntity.ok(new ApiResponse(Boolean.TRUE, "User details updated successfully"));

    }

    @GetMapping(path = "{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable("userId") Long userId){

        User user = userServiceImplementation.findById(userId);
        Optional<User> user1 = Optional.ofNullable(user);
        if (user1.isPresent()){
            return ResponseEntity.ok(user1.get());
        }
        return (ResponseEntity<User>) ResponseEntity.notFound();
    }


    @DeleteMapping(path = "{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Long userId, @CurrentUser UserPrincipal currentUser){

            ApiResponse res = userServiceImplementation.deleteUser(userId, currentUser);
            return ResponseEntity.ok(res);
    }

    @PostMapping
    @PreAuthorize("hasRole('ApiResponseDMIN')")
    public ResponseEntity<User> addUser(@Valid @RequestBody AddUserRequest user) {
        User newUser = userServiceImplementation.addUser(user);

        return new ResponseEntity< >(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/giveAdmin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> giveAdmin(@PathVariable(name = "userId") Long userId) {
        ApiResponse apiResponse = userServiceImplementation.giveAdmin(userId);

        return new ResponseEntity< >(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/takeAdmin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> takeAdmin(@PathVariable(name = "userId") Long userId) {
        ApiResponse apiResponse = userServiceImplementation.removeAdmin(userId);

        return new ResponseEntity< >(apiResponse, HttpStatus.OK);
    }

}
