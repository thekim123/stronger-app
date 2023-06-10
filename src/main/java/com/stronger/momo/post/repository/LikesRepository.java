package com.stronger.momo.post.repository;

import com.stronger.momo.post.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Modifying
    @Query(value = "insert into likes(postId, userId) values(:postId, :userId)", nativeQuery = true)
    void mLikes(Long postId, Long userId);

    @Modifying
    @Query(value = "delete from likes where postId = :postId and userId = :userId", nativeQuery = true)
    void mUnLikes(Long postId, Long userId);
}
