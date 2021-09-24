package com.dee.blog_rest.requests_and_responses;


import lombok.Data;

@Data
public class UserDetailsResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private int totalNumberOfConnections;
    private int totalNumberOfPosts;
    private String dateCreated;

}
