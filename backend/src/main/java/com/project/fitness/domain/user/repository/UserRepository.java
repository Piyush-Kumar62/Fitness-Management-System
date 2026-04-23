package com.project.fitness.domain.user.repository;

import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  User findByEmail(String email);

  Optional<User> findOptionalByEmail(String email);

  @Query("SELECT u FROM User u WHERE " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
  Page<User> searchUsers(@Param("query") String query, Pageable pageable);

  long countByRole(UserRole role);

  List<User> findByTrainerId(String trainerId);
  Page<User> findByTrainerId(String trainerId, Pageable pageable);

  List<User> findByRole(UserRole role);

  List<User> findByGymIdIn(List<String> gymIds);
  Page<User> findByGymIdInAndRole(List<String> gymIds, UserRole role, Pageable pageable);

  long countByGymIdInAndRole(List<String> gymIds, UserRole role);

  Page<User> findByRole(UserRole role, Pageable pageable);

  long countByActiveTrue();

  @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.active = true")
  long countByRoleAndActiveTrue(@Param("role") UserRole role);
}
