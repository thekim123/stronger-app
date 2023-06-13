package com.stronger.momo.post.repository;

import com.stronger.momo.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p " +
            "where " +
            "(p.team.isOpen=true and " +
            "p.writer.id in " +
            "(select s.toUser.id from Subscribe s where s.fromUser.id = ?1)) " +
            "or " +
            "p.team.id in " +
            "(select tm.team.id from TeamMember tm where tm.user.id= ?1) " +
            "order by p.createdAt desc")
    Page<Post> findMyFeed(Long loginUserId, Pageable pageable);
}