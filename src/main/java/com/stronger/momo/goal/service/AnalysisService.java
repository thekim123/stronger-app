package com.stronger.momo.goal.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.repository.GoalRepository;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.team.repository.TeamRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AnalysisService {

    private final GoalRepository goalRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> getAnalysis(Authentication authentication, Long teamId, String selectedDate) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        return teamMemberRepository.findByUser(loginUser);

    }
}
