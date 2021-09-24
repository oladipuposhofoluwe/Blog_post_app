package com.dee.blog_rest.repositories;

import com.dee.blog_rest.entities.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByUser_IdAndFollowing_Id(Long User_id, Long following_id);
    Follow findByFollowing_Id(Long follower_id);
    List<Follow> findAllByUserId(Long id);
    List<Follow> findAllByFollowing_Id(Long id);
}
