package com.project.fitness.repository;

import com.project.fitness.model.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

  // Property traversal: Activity.user.id
  List<Activity> findByUser_Id(String userId);
}
