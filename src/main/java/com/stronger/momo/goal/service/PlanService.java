package com.stronger.momo.goal.service;

import com.stronger.momo.goal.dto.PlanSaveDto;
import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.goal.repository.PlanRepository;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final TeamMemberRepository memberRepository;

    @Transactional
    public void createPlan(PlanSaveDto dto) {
        TeamMember member = memberRepository.findById(dto.getMemberId()).orElseThrow(() -> {
            throw new IllegalArgumentException("해당 멤버가 존재하지 않습니다.");
        });
        Plan plan = Plan.toCreate(dto);
        plan.setMember(member);
        planRepository.save(plan);
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
