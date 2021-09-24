package com.dee.blog_rest.services.serviceImplementation;

import com.dee.blog_rest.repositories.PostRepository;
import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.entities.Post;
import com.dee.blog_rest.entities.PostLike;
import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.repositories.PostLikeRepository;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostLikeService {

    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserServiceImplementation userServiceImplementation;

    @Autowired
    PostServiceImpl postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    public PostLikeService(PostLikeRepository postLikeRepository) {
        this.postLikeRepository = postLikeRepository;
    }


    public ApiResponse likePost(Long postId, UserPrincipal currentUser){
            Post post = postService.getPost(postId);
        if (post!=null){
            User byId = userServiceImplementation.findById(currentUser.getId());

            PostLike postLike1 = postLikeRepository.findByUser_Id(currentUser.getId());

           if (postLike1==null){
                PostLike postLike = new PostLike();
                postLike.setPost(post);
                postLike.setUser(byId);
//               post.setTotal_likes(post.getLikes().size());
                postLikeRepository.save(postLike);

               List<PostLike> allByPost_id = postLikeRepository.findAllByPost_Id(postId);
               post.setTotal_likes(allByPost_id.size());
               postRepository.save(post);

               return new ApiResponse(Boolean.TRUE, "Post Liked");
            }else if (postLike1!=null){
               Post post1 = postLike1.getPost();
               if (post1==post) {
                   postLikeRepository.delete(postLike1);
                   List<PostLike> allByPost_id = postLikeRepository.findAllByPost_Id(postId);
                   post.setTotal_likes(allByPost_id.size());
                   postRepository.save(post);
                   return new ApiResponse(Boolean.TRUE, "Post Unliked");
               }
           }


        }
        return new ApiResponse(Boolean.FALSE, "Could not like post");
    }

}
