package com.project.fitness.repository;

import com.project.fitness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

  User findByEmail(String email);

  @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
  org.springframework.data.domain.Page<User> searchUsers(@org.springframework.data.repository.query.Param("query") String query, org.springframework.data.domain.Pageable pageable);
}

