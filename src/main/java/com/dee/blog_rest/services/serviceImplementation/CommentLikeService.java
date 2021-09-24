package com.dee.blog_rest.services.serviceImplementation;

import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.entities.*;
import com.dee.blog_rest.repositories.CommentLikeRepository;
import com.dee.blog_rest.repositories.CommentRepository;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentLikeService {

    @Autowired
    UserServiceImplementation userServiceImplementation;

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    CommentRepository commentRepository;

    private CommentLikeRepository commentLikeRepository;

    @Autowired
    public CommentLikeService(CommentLikeRepository commentLikeRepository) {
        this.commentLikeRepository = commentLikeRepository;
    }


    public ApiResponse likeComment(Long commentId, UserPrincipal currentUser) {

        Optional<Comment> byId = commentRepository.findById(commentId);
        if (byId.isPresent()) {

            User user = userServiceImplementation.findById(currentUser.getId());

            CommentLike commentLikebyUser = commentLikeRepository.findByUser_Id(currentUser.getId());
            if (commentLikebyUser == null) {
                CommentLike commentLike = new CommentLike();
                commentLike.setComment(byId.get());
                commentLike.setUser(user);
                commentLikeRepository.save(commentLike);

                int size = commentLikeRepository.findAllByComment_Id(byId.get().getId()).size();
                System.out.println("TOTAL COMMENT LIKES IS "+size);
                byId.get().setTotal_likes(size);
                commentRepository.save(byId.get());

                return new ApiResponse(Boolean.TRUE, "Comment Liked");
            } else if (commentLikebyUser != null) {
                if (byId.get().equals(commentLikebyUser.getComment())) {
                    commentLikeRepository.delete(commentLikebyUser);
                    int size = commentLikeRepository.findAllByComment_Id(byId.get().getId()).size();
                    System.out.println("TOTAL COMMENT LIKES IS "+size);
                    byId.get().setTotal_likes(size);
                    commentRepository.save(byId.get());
                    return new ApiResponse(Boolean.TRUE, "Comment Unliked");
                }
            }
        }

        return new ApiResponse(Boolean.FALSE, "Could not like comment");
    }

}
