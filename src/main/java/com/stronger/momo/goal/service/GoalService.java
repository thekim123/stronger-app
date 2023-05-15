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
import java.util.stream.Stream;

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
     * @return 자신이 하는 모든 todo목록
     * @apiNote 자신이 하는 모든 todo를 가져오는 메서드
     * 로그인 유저 -> 로그인 유저 = 팀 멤버 -> 팀멤버로 가지고 있는 모든 goal
     */
    @Transactional(readOnly = true)
    public List<GoalDto> getTodoList(Authentication authentication) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        return getGoalsForUser(loginUser);
    }

    public List<GoalDto> getGoalsForUser(User user) {
        return teamMemberRepository.findByUser(user).stream()
                .flatMap(this::getGoalDtoList)
                .collect(Collectors.toList());
    }

    public Stream<GoalDto> getGoalDtoList(TeamMember teamMember) {
        return goalRepository.findByOwner(teamMember).stream()
                .map(goal -> {
                    dailyCheckRepository.findByGoalAndCheckDate(goal, LocalDate.now()).ifPresent(goal::addDailyCheck);
                    return GoalDto.fromGoal(goal);
                });
    }


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

        TeamMember teamMember = teamMemberRepository.findByUserAndTeam(user, team).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀의 멤버가 아닙니다");
        });

        List<Goal> goalList = goalRepository.findByOwner(teamMember);
        return goalList.stream().map(GoalDto::fromGoal).collect(Collectors.toList());
    }


    /**
     * 계획 생성 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 작성 dto
     */
    @Transactional
    public GoalDto createGoal(Authentication authentication, GoalCreateDto dto) {
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
                .team(team)
                .build();

        Goal insertedGoal = goalRepository.save(goal);
        return GoalDto.fromGoal(insertedGoal);
    }


    /**
     * 계획 삭제 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param goalId         계획 작성 dto
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @Transactional
    public void deleteGoal(Authentication authentication, Long goalId) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isGoalOwner(goalId, owner.getId());
        goalRepository.deleteById(goalId);
    }


    /**
     * 계획 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @Transactional
    public void updateGoal(Authentication authentication, GoalUpdateDto dto) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        if (dto.getId() != null) {
            isGoalOwner(dto.getId(), owner.getId());
        }
        Goal entity = goalRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("계획이 존재하지 않습니다.");
        });

        entity.update(dto);
    }


    /**
     * 오늘의 계획 완수 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param goalId         계획 id
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우, 이미 계획 완수를 누른 경우
     */
    @Transactional
    public void dailyCheck(Authentication authentication, Long goalId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isGoalOwner(goalId, user.getId());

        // TODO: isGoalOwner 에서 goal을 가져오는데, 다시 가져오는 것은 비효율적이다. 수정 필요
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
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isGoalOwner(goalId, user.getId());

        // TODO: isGoalOwner 에서 goal을 가져오는데, 다시 가져오는 것은 비효율적이다. 수정 필요
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        // TODO: 삭제하고 다시 누르면 이게 나옴.
        dailyCheckRepository.findByGoalAndCheckDate(goal, LocalDate.now()).ifPresentOrElse(
                dailyCheck -> dailyCheck.setCompleted(!dailyCheck.isCompleted()), () -> {
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
     * 교관 피드백 작성 서비스 메서드
     *
     * @param dto    교관 피드백 작성 dto
     * @param goalId 계획 id
     */
    @Transactional
    public void createFeedback(Authentication authentication, FeedbackDto dto, Long goalId) {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });
        isGoalInstructor(authentication, goalId);

        Feedback feedback = Feedback.builder()
                .user(user)
                .member(goal.getOwner())
                .comment(dto.getComment())
                .build();
        feedbackRepository.save(feedback);
    }

    /**
     * 교관 피드백 삭제 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param feedbackId     교관 피드백 id
     * @param goalId         계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void deleteFeedback(Authentication authentication, Long feedbackId, Long goalId) throws AccessDeniedException {
        isGoalInstructor(authentication, goalId);
        feedbackRepository.deleteById(feedbackId);
    }


    /**
     * 교관 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            교관 피드백 dto
     * @param goalId         계획 id
     * @throws AccessDeniedException 교관 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void updateFeedback(Authentication authentication, FeedbackDto dto, Long goalId) throws AccessDeniedException {
        isGoalInstructor(authentication, goalId);
        Feedback entity = feedbackRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 교관 피드백이 존재하지 않습니다");
        });
        entity.update(dto);
    }


    /**
     * 셀프 피드백 작성 서비스 메서드
     *
     * @param dto    셀프피드백 작성 dto
     * @param goalId 계획 id
     */
    @Transactional
    public void createSelfFeedback(SelfFeedbackDto dto, Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        SelfFeedback selfFeedback = SelfFeedback.builder()
                .reason(dto.getReason())
                .measure(dto.getMeasure())
                .member(goal.getOwner())
                .build();
        selfFeedbackRepository.save(selfFeedback);
    }


    /**
     * 셀프 피드백 삭제 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param selfId         셀프 피드백 id
     * @param goalId         계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void deleteSelfFeedback(Authentication authentication, Long selfId, Long goalId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isGoalOwner(goalId, user.getId());
        selfFeedbackRepository.deleteById(selfId);
    }


    /**
     * 셀프 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            셀프 피드백 dto
     * @param goalId         계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void updateSelfFeedback(Authentication authentication, SelfFeedbackDto dto, Long goalId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isGoalOwner(goalId, user.getId());
        SelfFeedback entity = selfFeedbackRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 셀프 피드백이 존재하지 않습니다");
        });

        entity.update(dto);
    }


    /**
     * 계획의 소유자인지 판정하는 메서드
     *
     * @param goalId  계획 글의 id
     * @param ownerId 게획 소유자의 id
     * @throws AccessDeniedException   계획의 소유자가 아닌 경우
     * @throws EntityNotFoundException 엔티티가 존재하지 않는 경우
     */
    public void isGoalOwner(Long goalId, Long ownerId) throws AccessDeniedException {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        User ownerUser = goal.getOwner().getUser();
        User currentUser = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 유저가 없습니다.");
        });

        if (!ownerUser.getUsername().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("당신의 계획이 아닙니다.");
        }
    }


    /**
     * 계획의 교관인지 판정하는 메서드
     *
     * @param authentication 유저(교관) 인증 정보
     * @param goalId         계획 id
     * @throws AccessDeniedException 계획의 교관이 아닌 경우
     */
    private void isGoalInstructor(Authentication authentication, Long goalId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> {
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
