package com.stronger.momo.goal.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.dto.AnalysisDto;
import com.stronger.momo.goal.dto.GoalAnalisysDto;
import com.stronger.momo.goal.dto.GoalDto;
import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Feedback;
import com.stronger.momo.goal.entity.Goal;
import com.stronger.momo.goal.entity.SelfFeedback;
import com.stronger.momo.goal.repository.FeedbackRepository;
import com.stronger.momo.goal.repository.GoalRepository;
import com.stronger.momo.goal.repository.SelfFeedbackRepository;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.team.repository.TeamRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AnalysisService {

    private final GoalRepository goalRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final FeedbackRepository feedbackRepository;
    private final SelfFeedbackRepository selfFeedbackRepository;

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public AnalysisDto getAnalysis(Authentication authentication, Long teamId, String selectedDate) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_DATE);
        LocalDate startDate = date.with(DayOfWeek.MONDAY);
        LocalDate endDate = date.with(DayOfWeek.SUNDAY);

        Team team = findTeamById(teamId);
        TeamMember teamMember = findTeamMember(loginUser, team);
        List<Goal> goals = teamMember.getGoal();

        List<GoalAnalisysDto> goalAnalisysDto = goals.stream()
                .map(g -> {
                    List<DailyCheck> dailyCheckList = entityManager.createQuery(
                                    "SELECT d FROM DailyCheck d " +
                                            "JOIN Goal g ON d.goal.id = g.id " +
                                            "WHERE g.id= :goalId " +
                                            "AND d.checkDate >= :startDate " +
                                            "AND d.checkDate <= :endDate", DailyCheck.class)
                            .setParameter("goalId", g.getId())
                            .setParameter("startDate", startDate)
                            .setParameter("endDate", endDate)
                            .getResultList();

                    return GoalAnalisysDto.mapDailyCheck(g, dailyCheckList);
                })
                .collect(Collectors.toList());

        Feedback feedback = feedbackRepository
                .findByMemberAndCheckDateBetween(teamMember, startDate, endDate)
                .orElse(new Feedback());

        SelfFeedback selfFeedback = selfFeedbackRepository
                .findByMemberAndCheckDateBetween(teamMember, startDate, endDate)
                .orElse(new SelfFeedback());

        AnalysisDto result = AnalysisDto.builder()
                .goalList(goalAnalisysDto)
                .feedback(feedback.toDto())
                .selfFeedback(selfFeedback.toDto())
                .build();

        System.out.println(result);
        return result;
    }

    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found."));
    }

    public TeamMember findTeamMember(User user, Team team) {
        return teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found."));
    }

}
