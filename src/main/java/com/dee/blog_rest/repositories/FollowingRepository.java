package com.dee.blog_rest.repositories;

import com.dee.blog_rest.entities.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingRepository extends JpaRepository<Follow, Long> {
    Follow findByFollowing(Long id);
}
