package com.dee.blog_rest.repositories;

import com.dee.blog_rest.entities.CommentLike;
import com.dee.blog_rest.entities.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByUser_Id(Long userId);
    List<CommentLike> findAllByComment_Id(Long commentId);
}