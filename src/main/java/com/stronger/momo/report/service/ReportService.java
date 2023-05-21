package com.stronger.momo.report.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.goal.repository.PlanRepository;
import com.stronger.momo.report.dto.FeedbackDto;
import com.stronger.momo.report.dto.ReportDto;
import com.stronger.momo.report.dto.SelfFeedbackDto;
import com.stronger.momo.report.entity.Feedback;
import com.stronger.momo.report.entity.SelfFeedback;
import com.stronger.momo.report.repository.FeedbackRepository;
import com.stronger.momo.goal.repository.GoalRepository;
import com.stronger.momo.report.repository.SelfFeedbackRepository;
import com.stronger.momo.team.entity.Grade;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.team.repository.TeamRepository;
import com.stronger.momo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final GoalRepository goalRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PlanRepository planRepository;

    private final FeedbackRepository feedbackRepository;
    private final SelfFeedbackRepository selfFeedbackRepository;


    private final EntityManager entityManager;

    //TODO: Entity 개편 후 수정필요함.
    @Transactional(readOnly = true)
    public ReportDto getAnalysis(Authentication authentication, Long teamId, String selectedDate) {
//        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
//        LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_DATE);
//        LocalDate startDate = date.with(DayOfWeek.MONDAY);
//        LocalDate endDate = date.with(DayOfWeek.SUNDAY);
//
//        Team team = findTeamById(teamId);
//        TeamMember teamMember = findTeamMember(loginUser, team);
//        List<Goal> goals = teamMember.getPlanList().forEach(p -> p.getGoalList());
//
//        List<GoalReportDto> goalReportDto = goals.stream()
//                .map(g -> {
//                    List<DailyCheck> dailyCheckList = entityManager.createQuery(
//                                    "SELECT d FROM DailyCheck d " +
//                                            "JOIN Goal g ON d.goal.id = g.id " +
//                                            "WHERE g.id= :goalId " +
//                                            "AND d.checkDate >= :startDate " +
//                                            "AND d.checkDate <= :endDate", DailyCheck.class)
//                            .setParameter("goalId", g.getId())
//                            .setParameter("startDate", startDate)
//                            .setParameter("endDate", endDate)
//                            .getResultList();
//
//                    return GoalReportDto.mapDailyCheck(g, dailyCheckList);
//                })
//                .collect(Collectors.toList());
//
//        Feedback feedback = feedbackRepository
//                .findByMemberAndCheckDateBetween(teamMember, startDate, endDate)
//                .orElse(new Feedback());
//
//        SelfFeedback selfFeedback = selfFeedbackRepository
//                .findByMemberAndCheckDateBetween(teamMember, startDate, endDate)
//                .orElse(new SelfFeedback());
//
//        ReportDto result = ReportDto.builder()
//                .goalList(goalReportDto)
//                .feedback(feedback.toDto())
//                .selfFeedback(selfFeedback.toDto())
//                .build();
//
//        System.out.println(result);
//        return result;
        return null;
    }

    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found."));
    }

    public TeamMember findTeamMember(User user, Team team) {
        return teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found."));
    }


    /**
     * @param dto 교관 피드백 작성 dto
     * @apiNote 교관 피드백 작성 서비스 메서드
     */
    @Transactional
    public FeedbackDto createFeedback(Authentication authentication, FeedbackDto dto) {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Plan plan = planRepository.findById(dto.getPlanId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        Team team = plan.getMember().getTeam();
        TeamMember member = teamMemberRepository.findByUserAndTeam(user, team).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 DB에 없습니다.");
        });
        isGoalInstructor(member.getGrade());

        Feedback feedback = Feedback.builder()
                .user(user)
                .plan(plan)
                .comment(dto.getComment())
                .checkDate(LocalDate.now())
                .build();
        feedbackRepository.save(feedback);
        return FeedbackDto.fromFeedback(feedback);
    }

    /**
     * @param feedbackId 교관 피드백 id
     * @param memberId   계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     * @apiNote 교관 피드백 삭제 서비스 메서드
     */
    @Transactional
    public void deleteFeedback(Long feedbackId, Long memberId) throws AccessDeniedException {
        TeamMember member = teamMemberRepository.findById(memberId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 DB에 없습니다.");
        });

        isGoalInstructor(member.getGrade());
        feedbackRepository.deleteById(feedbackId);
    }


    /**
     * 교관 피드백 수정 서비스 메서드
     *
     * @param dto      교관 피드백 dto
     * @param memberId 팀원 id
     * @throws AccessDeniedException 교관 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void updateFeedback(FeedbackDto dto, Long memberId) throws AccessDeniedException {
        TeamMember member = teamMemberRepository.findById(memberId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 DB에 없습니다.");
        });
        isGoalInstructor(member.getGrade());
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
    public SelfFeedbackDto createSelfFeedback(SelfFeedbackDto dto, Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 DB에 없습니다.");
        });

        SelfFeedback selfFeedback = SelfFeedback.builder()
                .reason(dto.getReason())
                .measure(dto.getMeasure())
                .checkDate(dto.getCheckDate())
                .plan(plan)
                .build();
        selfFeedbackRepository.save(selfFeedback);
        return SelfFeedbackDto.fromSelfFeedback(selfFeedback);
    }


    /**
     * @param authentication 유저 인증 정보
     * @param selfId         셀프 피드백 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     * @apiNote 셀프 피드백 삭제 서비스 메서드
     */
    @Transactional
    public void deleteSelfFeedback(
            Authentication authentication,
            Long selfId) throws AccessDeniedException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        SelfFeedback selfFeedback = selfFeedbackRepository.findById(selfId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 셀프 피드백이 존재하지 않습니다");
        });
        String selfUsername = selfFeedback.getPlan().getMember().getUser().getUsername();

        if (!user.getUsername().equals(selfUsername)) {
            throw new AccessDeniedException("해당 셀프 피드백의 소유자가 아닙니다.");
        }

        selfFeedbackRepository.deleteById(selfId);
    }


    /**
     * 셀프 피드백 수정 서비스 메서드
     *
     * @param dto 셀프 피드백 dto
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @Transactional
    public void updateSelfFeedback(SelfFeedbackDto dto) throws AccessDeniedException {
        SelfFeedback entity = selfFeedbackRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 셀프 피드백이 존재하지 않습니다");
        });

        entity.update(dto);
    }


    /**
     * @throws AccessDeniedException 계획의 매니저가 아닌 경우
     * @apiNote 계획의 매니저인지 판정하는 메서드
     */
    private void isGoalInstructor(Grade grade) throws AccessDeniedException {
        if (grade.equals(Grade.MEMBER) || grade.equals(Grade.PENDING)) {
            throw new AccessDeniedException("해당 계획의 교관이 아닙니다.");
        }
    }


}
