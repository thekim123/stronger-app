package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
