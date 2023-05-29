package com.stronger.momo.report.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.dto.DailyCheckDto;
import com.stronger.momo.goal.dto.PlanDto;
import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Goal;
import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.goal.repository.DailyCheckRepository;
import com.stronger.momo.goal.repository.GoalRepository;
import com.stronger.momo.goal.repository.PlanRepository;
import com.stronger.momo.report.dto.ReportDto;
import com.stronger.momo.report.entity.Feedback;
import com.stronger.momo.report.entity.SelfFeedback;
import com.stronger.momo.report.repository.FeedbackRepository;
import com.stronger.momo.report.repository.ReportRepository;
import com.stronger.momo.report.repository.SelfFeedbackRepository;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.team.repository.TeamRepository;
import com.stronger.momo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final FeedbackRepository feedbackRepository;
    private final SelfFeedbackRepository selfFeedbackRepository;
    private final DailyCheckRepository dailyCheckRepository;
    private final PlanRepository planRepository;


    @Transactional(readOnly = true)
    public ReportDto getAnalysisData(Authentication authentication, Long planId, String selectedDate) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_DATE);
        LocalDate startDate = date.with(DayOfWeek.MONDAY);
        LocalDate endDate = date.with(DayOfWeek.SUNDAY);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(EntityNotFoundException::new);

        List<DailyCheck> dailyChecks = dailyCheckRepository.findByPlanId(planId, startDate, endDate);
        PlanDto planDto = PlanDto.whenMakeReport(plan, dailyChecks);

        Feedback feedback = feedbackRepository
                .getFeedbackForReport(planId, startDate, endDate)
                .orElse(new Feedback());
        SelfFeedback selfFeedback = selfFeedbackRepository
                .getSelfFeedbackForReport(planId, startDate, endDate)
                .orElse(new SelfFeedback());

        return ReportDto.builder()
                .feedback(feedback.toDto())
                .selfFeedback(selfFeedback.toDto())
                .plan(planDto)
                .build();
    }


}
