package com.stronger.momo.team.repository;

import com.stronger.momo.team.entity.Team;
import com.stronger.momo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByTeamCode(String teamCode);

    @Query("select t.id from Team  t " +
            "inner join t.teamMemberList tm on tm.id= :userId " +
            "where t.isOpen= true")
    List<Long> findTeamIdList(Long userId);
}
