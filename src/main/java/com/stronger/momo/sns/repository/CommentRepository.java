package com.stronger.momo.sns.repository;

import com.stronger.momo.sns.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
