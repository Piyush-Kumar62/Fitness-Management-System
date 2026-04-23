package com.project.fitness.domain.fitness.repository;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.fitness.model.Activity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

  // Property traversal: Activity.user.id
  List<Activity> findByUser_Id(String userId);

  // Paginated version
  Page<Activity> findByUser_Id(String userId, Pageable pageable);
}
