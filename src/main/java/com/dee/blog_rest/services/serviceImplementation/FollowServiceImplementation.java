package com.dee.blog_rest.services.serviceImplementation;

import com.dee.blog_rest.entities.Follow;
import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.exceptions.BadRequestException;
import com.dee.blog_rest.repositories.FollowRepository;
import com.dee.blog_rest.repositories.UserRepository;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.security.CurrentUser;
import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.services.FollowService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FollowServiceImplementation implements FollowService {

    private FollowRepository followRepository;
    private UserRepository userRepository;


    @Override
    public List<User> getUserFollowers(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            List<Follow> allByUserId = followRepository.findAllByFollowing_Id(userId);

            List<User> following = new ArrayList<>();
            allByUserId.stream().forEachOrdered(follow -> {
                following.add(follow.getUser());
            });
            System.out.println(following);
            return following;
        }
        throw new BadRequestException("Could not get following");
    }

    @Override
    public List<User> getUserFollowing(Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            List<Follow> allByUserId = followRepository.findAllByUserId(userId);
            List<User> following = new ArrayList<>();
            allByUserId.stream().forEachOrdered(follow -> {
                following.add(follow.getFollowing());
            });
            System.out.println(following);
            return following;
        }

        throw new BadRequestException("Could not get following");
    }

    @Transactional
    @Override
    public ApiResponse follow(Long idToFollow, UserPrincipal currentUser) {
        User otherUser = userRepository.getById(idToFollow);
        if (otherUser != null && idToFollow != currentUser.getId()) {
            User thisUser = userRepository.getById(currentUser.getId());

            Follow byFollower = followRepository.findByUser_IdAndFollowing_Id(thisUser.getId(), otherUser.getId());

            if (byFollower!=null) {

                return new ApiResponse(Boolean.TRUE,
                        "You are ALREADY following " + otherUser.getFirstName() + " " +
                                otherUser.getLastName());
            }

            Follow follow = new Follow(thisUser, otherUser);
            List<Follow> followList = thisUser.getAllFollow();
            boolean followed = followList.add(follow);

            if (followed) {
                follow.setCreatedAt(Instant.now());
                follow.setUpdatedAt(Instant.now());
                return new ApiResponse(Boolean.TRUE,
                        "You are now following " + otherUser.getFirstName() + " " +
                                otherUser.getLastName());
            }

        }

        return new ApiResponse(Boolean.FALSE, "Error following " + otherUser.getFirstName() + " " +
                otherUser.getLastName());
    }

    @Transactional
    @Override
    public ApiResponse unfollow(Long idToUnfollow, UserPrincipal currentUser) {
        User otherUser = userRepository.getById(idToUnfollow);
        if (otherUser != null && idToUnfollow != currentUser.getId()) {
            User thisUser = userRepository.getById(currentUser.getId());

            Follow byFollower = followRepository.findByUser_IdAndFollowing_Id(thisUser.getId(), otherUser.getId());

            if(byFollower==null){
                return new ApiResponse(Boolean.FALSE, "You are NOT following " + otherUser.getFirstName() + " " +
                        otherUser.getLastName() + ", so you can't possibly unfollow them");
            }

            Long followingId = byFollower.getFollowing().getId();
            Long userId2 = byFollower.getUser().getId();


            if ( userId2 == thisUser.getId() && followingId == otherUser.getId()) {
                followRepository.delete(byFollower);
                return new ApiResponse(Boolean.TRUE,
                        "You have unfollowed " + otherUser.getFirstName() + " " +
                                otherUser.getLastName());
            }

        }
        return new ApiResponse(Boolean.FALSE, "Error unfollowing " + otherUser.getFirstName() + " " +
                otherUser.getLastName());

    }
}
