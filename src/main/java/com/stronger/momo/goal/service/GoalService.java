package com.stronger.momo.goal.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.dto.*;
import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Feedback;
import com.stronger.momo.goal.entity.Goal;
import com.stronger.momo.goal.entity.SelfFeedback;
import com.stronger.momo.goal.repository.DailyCheckRepository;
import com.stronger.momo.goal.repository.FeedbackRepository;
import com.stronger.momo.goal.repository.GoalRepository;
import com.stronger.momo.goal.repository.SelfFeedbackRepository;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.entity.Grade;
import com.stronger.momo.team.repository.TeamRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.user.repository.UserRepository;
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

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final DailyCheckRepository dailyCheckRepository;
    private final SelfFeedbackRepository selfFeedbackRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;


    /**
     * @param authentication 유저 인증 정보
     * @param teamId         팀 id
     * @return 팀원 목표 조회 dto
     * @apiNote 해당 팀원의 목표 조회 서비스 메서드
     */
    @Transactional(readOnly = true)
    public List<GoalDto> findGoalByTeam(Authentication authentication, Long teamId) {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(teamId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀이 존재하지 않습니다");
        });
        System.out.println(team.getGroupName());
        TeamMember teamMember = teamMemberRepository.findByUserAndTeam(user, team).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀의 멤버가 아닙니다");
        });
        System.out.println(teamMember.getUser().getUsername());
        List<Goal> goalList = goalRepository.mfindByOwnerAndDate(teamMember, LocalDate.now());
        System.out.println(goalList.size());
        return goalList.stream().map(GoalDto::fromGoal).collect(Collectors.toList());
    }


    /**
     * 계획 생성 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 작성 dto
     */
    @Transactional
    public void createPlan(Authentication authentication, GoalCreateDto dto) {
        User member = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀이 존재하지 않습니다");
        });

        TeamMember teamMember = teamMemberRepository.findByUserAndTeam(member, team).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀의 멤버가 아닙니다.");
        });

        if (Objects.equals(teamMember.getGrade(), Grade.PENDING)) {
            throw new AccessDeniedException("대기자는 계획을 작성할 수 없습니다");
        }

        Goal goal = Goal.builder()
                .owner(teamMember)
                .actionCount(0)
                .currentWeeks(0)
                .title(dto.getTitle())
                .content(dto.getContent())
                .goalCount(dto.getGoalCount())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .team(team)
                .build();
        goalRepository.save(goal);
    }


    /**
     * 계획 삭제 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param planId         계획 작성 dto
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @Transactional
    public void deletePlan(Authentication authentication, Long planId) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isPlanOwner(planId, owner.getId());
        goalRepository.deleteById(planId);
    }


    /**
     * 계획 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @Transactional
    public void updatePlan(Authentication authentication, GoalUpdateDto dto) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isPlanOwner(dto.getId(), owner.getId());
        Goal entity = goalRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("계획이 존재하지 않습니다.");
        });

        entity.update(dto);
    }


    /**
     * 오늘의 계획 완수 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param planId         계획 id
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우, 이미 계획 완수를 누른 경우
     */
    @Transactional
    public void dailyCheck(Authentication authentication, Long planId) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isPlanOwner(planId, owner.getId());

        Goal goal = goalRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        LocalDate checkDate = LocalDate.now();
        if (dailyCheckRepository.findByGoalAndCheckDate(goal, checkDate).isPresent()) {
            throw new AccessDeniedException("오늘은 이미 완수했어요!!");
        }

        DailyCheck dailyCheck = DailyCheck.builder()
                .weeks(goal.getCurrentWeeks())
                .checkDate(checkDate)
                .isCompleted(true)
                .goal(goal)
                .build();

        goal.setActionCount(goal.getActionCount() + 1);
        dailyCheckRepository.save(dailyCheck);
    }


    /**
     * 교관 피드백 작성 서비스 메서드
     *
     * @param dto    교관 피드백 작성 dto
     * @param planId 계획 id
     */
    @Transactional
    public void createFeedback(Authentication authentication, FeedbackDto dto, Long planId) {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        Goal goal = goalRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });
        isPlanInstructor(authentication, planId);

        Feedback feedback = Feedback.builder()
                .goal(goal)
                .user(user)
                .comment(dto.getComment())
                .build();
        feedbackRepository.save(feedback);
    }

    /**
     * 교관 피드백 삭제 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param feedbackId     교관 피드백 id
     * @param planId         계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void deleteFeedback(Authentication authentication, Long feedbackId, Long planId) throws AccessDeniedException {
        isPlanInstructor(authentication, planId);
        feedbackRepository.deleteById(feedbackId);
    }


    /**
     * 교관 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            교관 피드백 dto
     * @param planId         계획 id
     * @throws AccessDeniedException 교관 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void updateFeedback(Authentication authentication, FeedbackDto dto, Long planId) throws AccessDeniedException {
        isPlanInstructor(authentication, planId);
        Feedback entity = feedbackRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 교관 피드백이 존재하지 않습니다");
        });
        entity.update(dto);
    }


    /**
     * 셀프 피드백 작성 서비스 메서드
     *
     * @param dto    셀프피드백 작성 dto
     * @param planId 계획 id
     */
    @Transactional
    public void createSelfFeedback(SelfFeedbackDto dto, Long planId) {
        Goal goal = goalRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        SelfFeedback selfFeedback = SelfFeedback.builder()
                .reason(dto.getReason())
                .measure(dto.getMeasure())
                .goal(goal)
                .build();
        selfFeedbackRepository.save(selfFeedback);
    }


    /**
     * 셀프 피드백 삭제 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param selfId         셀프 피드백 id
     * @param planId         계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void deleteSelfFeedback(Authentication authentication, Long selfId, Long planId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isPlanOwner(planId, user.getId());
        selfFeedbackRepository.deleteById(selfId);
    }


    /**
     * 셀프 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            셀프 피드백 dto
     * @param planId         계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void updateSelfFeedback(Authentication authentication, SelfFeedbackDto dto, Long planId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isPlanOwner(planId, user.getId());
        SelfFeedback entity = selfFeedbackRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 셀프 피드백이 존재하지 않습니다");
        });

        entity.update(dto);
    }


    /**
     * 계획의 소유자인지 판정하는 메서드
     *
     * @param planId  계획 글의 id
     * @param ownerId 게획 소유자의 id
     * @throws AccessDeniedException   계획의 소유자가 아닌 경우
     * @throws EntityNotFoundException 엔티티가 존재하지 않는 경우
     */
    public void isPlanOwner(Long planId, Long ownerId) throws AccessDeniedException {
        Goal goal = goalRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        User currentUser = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 유저가 없습니다.");
        });

        // TODO : 현재 유저와 계획의 소유자가 같은지 판단하는 로직을 다시 짜야함.
        if (goal.getOwner().equals(currentUser)) {
            return;
        }
        throw new AccessDeniedException("당신의 계획이 아닙니다.");
    }


    /**
     * 계획의 교관인지 판정하는 메서드
     *
     * @param authentication 유저(교관) 인증 정보
     * @param planId         계획 id
     * @throws AccessDeniedException 계획의 교관이 아닌 경우
     */
    private void isPlanInstructor(Authentication authentication, Long planId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Goal goal = goalRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획을 찾을 수 없습니다.");
        });

        TeamMember teamMember = teamMemberRepository.findByUserAndTeam(user, goal.getTeam()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀의 멤버가 아닙니다.");
        });
        Grade grade = teamMember.getGrade();
        if (grade.equals(Grade.MEMBER) || grade.equals(Grade.PENDING)) {
            throw new AccessDeniedException("해당 계획의 교관이 아닙니다.");
        }
    }

}
