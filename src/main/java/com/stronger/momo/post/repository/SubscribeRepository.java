package com.stronger.momo.post.repository;

import com.stronger.momo.post.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Modifying
    @Query(value = "INSERT INTO subscribe(fromUserId, toUserId, createdAt) VALUES(:fromUserId, :toUserId, now())", nativeQuery = true)
    void subscribe(Long fromUserId, Long toUserId);

    @Modifying
    @Query(value = "delete from subscribe where fromUserId = :fromUserId and toUserId = :toUserId", nativeQuery = true)
    void unSubscribe(Long fromUserId, Long toUserId);

}
