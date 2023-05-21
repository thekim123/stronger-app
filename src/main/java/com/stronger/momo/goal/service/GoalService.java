package com.stronger.momo.goal.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.dto.*;
import com.stronger.momo.goal.entity.*;
import com.stronger.momo.goal.repository.*;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.entity.Grade;
import com.stronger.momo.team.repository.TeamRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final DailyCheckRepository dailyCheckRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final PlanRepository planRepository;


    /**
     * @param authentication 유저 인증 정보
     * @return 자신이 하는 모든 todo목록
     * @apiNote 자신이 하는 모든 todo를 가져오는 메서드
     * 로그인 유저 -> 로그인 유저 = 팀 멤버 -> 팀멤버로 가지고 있는 모든 goal
     */
    @Transactional(readOnly = true)
    public List<GoalDto> getTodoList(Authentication authentication, Long teamId) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(teamId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀이 존재하지 않습니다.");
        });
        TeamMember member = getTeamMemberForPlan(loginUser, team);
        return getGoalsForPlan(member);
    }

    public TeamMember getTeamMemberForPlan(User user, Team team) {
        return teamMemberRepository.findByUserAndTeam(user, team).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 존재하지 않습니다.");
        });
    }

    public List<GoalDto> getGoalsForPlan(TeamMember member) {
        return planRepository.findByMember(member).stream()
                .flatMap(this::getGoalDtoList)
                .collect(Collectors.toList());
    }

    public Stream<GoalDto> getGoalDtoList(Plan plan) {
        return goalRepository.findByPlan(plan).stream()
                .map(goal -> {
                    dailyCheckRepository.findByGoalAndCheckDate(goal, LocalDate.now()).ifPresent(goal::addDailyCheck);
                    return GoalDto.fromGoal(goal);
                });
    }


    /**
     * @param memberId 팀 멤버 id
     * @return 팀원 계획 조회 dto
     * @apiNote 해당 팀원의 목표 조회 서비스 메서드
     */
    @Transactional(readOnly = true)
    public List<PlanDto> findGoalByMember(Long memberId) {
        TeamMember member = teamMemberRepository.findById(memberId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 존재하지 않습니다");
        });
        List<Plan> planList = planRepository.findByMember(member);

        return planList.stream().map(PlanDto::fromPlan).collect(Collectors.toList());
    }


    //TODO: 테스트 아직 안함

    /**
     * @param authentication 유저 인증 정보
     * @param dto            계획 생성 dto
     * @return 생성된 목표 dto
     * @apiNote 계획 생성 서비스 메서드
     */
    @Transactional
    public GoalDto createGoal(Authentication authentication, GoalCreateDto dto) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        TeamMember member = checkPlanExists(dto.getPlanId());

        if (!isPlanOwner(loginUser, member)) {
            throw new AccessDeniedException("자신의 계획에만 목표를 작성할 수 있습니다");
        }
        if (isGradePending(member)) {
            throw new AccessDeniedException("대기자는 목표를 작성할 수 없습니다");
        }

        Plan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("해당 계획이 존재하지 않습니다"));

        Goal goal = Goal.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .goalCount(dto.getGoalCount())
                .plan(plan)
                .build();

        Goal insertedGoal = goalRepository.save(goal);
        return GoalDto.fromGoal(insertedGoal);
    }

    public boolean isPlanOwner(User loginUser, TeamMember member) {
        return Objects.equals(loginUser.getUsername(), member.getUser().getUsername());
    }

    public boolean isGradePending(TeamMember member) {
        return member.getGrade().equals(Grade.PENDING);
    }

    private TeamMember checkPlanExists(Long planId) {
        return teamMemberRepository.getMemberPlanAndGoals(planId)
                .orElseThrow(() -> new EntityNotFoundException("해당 계획이 존재하지 않습니다"));
    }


    /**
     * @param authentication 유저 인증 정보
     * @param goalId         계획 작성 dto
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     * @apiNote 계획 삭제 서비스 메서드
     */
    @Transactional
    public void deleteGoal(Authentication authentication, Long goalId) throws AccessDeniedException {
        String loginUsername = ((PrincipalDetails) authentication.getPrincipal()).getUser().getUsername();
        isGoalOwner(goalId, loginUsername);
        goalRepository.deleteById(goalId);
    }


    /**
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     * @apiNote 계획 수정 서비스 메서드
     */
    @Transactional
    public void updateGoal(Authentication authentication, GoalUpdateDto dto) throws AccessDeniedException {
        String ownerUsername = ((PrincipalDetails) authentication.getPrincipal()).getUser().getUsername();
        isGoalOwner(dto.getId(), ownerUsername);
        Goal entity = goalRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("계획이 존재하지 않습니다.");
        });
        entity.update(dto);
    }


    /**
     * @param authentication 유저 인증 정보
     * @param goalId         계획 id
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우, 이미 계획 완수를 누른 경우
     * @apiNote 오늘의 계획 완수 서비스 메서드
     * {@link Goal}을 두 번 호출하는게 비효율적이긴 하지만, 함수를 하나 더 만드는게 맞나 싶다.
     * 분명 나중에 유지보수하면서 함수가 두 개가 있으면 왜 두 개지 하면서 볼텐데, 그게 너무 불편하다.
     * 당장에는 사용자가 그렇게 많지도 않을테니, 나중에 성능에 문제가 생기면 수정하자.
     */
    @Transactional
    public void dailyCheck(Authentication authentication, Long goalId) throws AccessDeniedException {
        String loginUsername = ((PrincipalDetails) authentication.getPrincipal()).getUser().getUsername();
        isGoalOwner(goalId, loginUsername);
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        if (dailyCheckRepository.findByGoalAndCheckDate(goal, LocalDate.now()).isPresent()) {
            throw new EntityNotFoundException("이미 오늘의 계획 완수를 누르셨습니다.");
        }

        LocalDate checkDate = LocalDate.now();
        DailyCheck dailyCheck = DailyCheck.builder()
                .weeks(goal.getCurrentWeeks())
                .checkDate(checkDate)
                .isCompleted(true)
                .goal(goal)
                .build();

        dailyCheckRepository.save(dailyCheck);
    }

    @Transactional
    public void updateDailyCheck(Authentication authentication, Long goalId) throws AccessDeniedException {
        String username = ((PrincipalDetails) authentication.getPrincipal()).getUser().getUsername();
        isGoalOwner(goalId, username);

        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 목표가 DB에 없습니다.");
        });

        dailyCheckRepository.findByGoalAndCheckDate(goal, LocalDate.now())
                .ifPresentOrElse(dailyCheck ->
                                dailyCheck.setCompleted(!dailyCheck.isCompleted()), () -> {
                            dailyCheckRepository.save(DailyCheck.builder()
                                    .weeks(goal.getCurrentWeeks())
                                    .checkDate(LocalDate.now())
                                    .isCompleted(true)
                                    .goal(goal)
                                    .build());
                        }
                );
    }


    /**
     * @param goalId        계획 글의 id
     * @param loginUsername 로그인한 유저네임
     * @throws AccessDeniedException   계획의 소유자가 아닌 경우
     * @throws EntityNotFoundException 엔티티가 존재하지 않는 경우
     * @apiNote 계획의 소유자인지 판정하는 메서드
     */
    public void isGoalOwner(Long goalId, String loginUsername) throws AccessDeniedException {
        Goal goal = goalRepository.findGoalWithOwner(goalId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });
        String ownerUsername = goal.getPlan().getMember().getUser().getUsername();

        if (!ownerUsername.equals(loginUsername)) {
            throw new AccessDeniedException("당신의 계획이 아닙니다.");
        }
    }

}
