package com.dee.blog_rest.repositories;

import com.dee.blog_rest.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long>{

    List<Comment> findCommentsByPostId(Long postId);
    Page<Comment> findByPostId(Long postId, Pageable pageable);

}