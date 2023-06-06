package com.stronger.momo.sns.repository;

import com.stronger.momo.sns.entity.Sns;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SnsRepository extends JpaRepository<Sns, Long> {

    @Query("select s from Sns s " +
            "where s.writer.id in " +
            "(select s.toUser.id from Subscribe s where s.fromUser.id = ?1) " +
            "order by s.createdAt desc")
    Page<Sns> findMyFeed(Long loginUserId, Pageable pageable);
}
