package com.dee.blog_rest.repositories;

import com.dee.blog_rest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE u.email = ?1")
  User findByEmail(String email);

//  @Query("SELECT u FROM user_connection u WHERE u.email = ?1")
//  User findConnections(String email);

  Boolean existsByEmail(@NotBlank String email);


}
