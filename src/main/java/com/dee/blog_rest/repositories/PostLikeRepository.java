package com.dee.blog_rest.repositories;

import com.dee.blog_rest.entities.CommentLike;
import com.dee.blog_rest.entities.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByUser_Id(Long userId);
    List<PostLike> findAllByPost_Id(Long postId);
}