package com.stronger.momo.sns.repository;

import com.stronger.momo.sns.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnsRepository extends JpaRepository<Sns, Long> {
}
