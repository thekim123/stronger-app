package com.stronger.momo.goal.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.dto.PlanDto;
import com.stronger.momo.goal.dto.PlanSaveDto;
import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.goal.repository.PlanRepository;
import com.stronger.momo.team.dto.TeamMemberDto;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final TeamMemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<TeamMemberDto> retrieveTeamList(
            Authentication authentication, Long memberId) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        List<TeamMember> members = memberRepository.findAllByUser(loginUser);


        List<Plan> plan = planRepository.findAllByMemberId(memberId);
//        return plan.stream().map(PlanDto::from).collect(Collectors.toList());
        return members.stream().map(TeamMemberDto::from).collect(Collectors.toList());
    }

    @Transactional
    public PlanDto createPlan(PlanSaveDto dto) {
        TeamMember member = memberRepository
                .findById(dto.getMemberId()).orElseThrow(() -> {
                    throw new EntityNotFoundException("해당 멤버가 존재하지 않습니다.");
                });
        Plan plan = Plan.toCreate(dto);
        plan.setMember(member);
        planRepository.save(plan);
        return PlanDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .memberId(dto.getMemberId())
                .build();
    }

    @Transactional
    public void deletePlan(Long planId) {
        planRepository.deleteById(planId);
    }

    @Transactional
    public void updatePlan(PlanSaveDto dto) {
        Plan plan = planRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new IllegalArgumentException("해당 계획이 존재하지 않습니다.");
        });
        plan.update(dto);
    }

}
