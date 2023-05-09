package com.stronger.momo.plan.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.plan.dto.FeedbackDto;
import com.stronger.momo.plan.dto.PlanCreateDto;
import com.stronger.momo.plan.dto.PlanUpdateDto;
import com.stronger.momo.plan.dto.SelfFeedbackDto;
import com.stronger.momo.plan.entity.DailyCheck;
import com.stronger.momo.plan.entity.Feedback;
import com.stronger.momo.plan.entity.Plan;
import com.stronger.momo.plan.entity.SelfFeedback;
import com.stronger.momo.plan.repository.DailyCheckRepository;
import com.stronger.momo.plan.repository.FeedbackRepository;
import com.stronger.momo.plan.repository.PlanRepository;
import com.stronger.momo.plan.repository.SelfFeedbackRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final DailyCheckRepository dailyCheckRepository;
    private final SelfFeedbackRepository selfFeedbackRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;


    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     */
    @Transactional(readOnly = true)
    public List<Plan> getPlan(Authentication authentication) {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        return planRepository.mFindTodoList(owner, LocalDate.now());
    }

    /**
     * 계획 생성 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 작성 dto
     */
    @Transactional
    public void createPlan(Authentication authentication, PlanCreateDto dto) {
        Plan entity = dto.toEntity();
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        entity.setOwner(owner);
        entity.setActionCount(0);
        entity.setCurrentWeeks(0);

        LocalDate startDate = dto.getStartDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        entity.setStartDate(startDate);
        entity.setEndDate(startDate.plusWeeks(dto.getTotalWeeks()));
        planRepository.save(entity);
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
        planRepository.deleteById(planId);
    }


    /**
     * 계획 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @Transactional
    public void updatePlan(Authentication authentication, PlanUpdateDto dto) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        isPlanOwner(dto.getId(), owner.getId());
        Plan entity = dto.toEntity();

        entityManager.merge(entity);
        entityManager.flush();
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

        Plan plan = planRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        if (dailyCheckRepository.findByPlanAndWeeksAndDayOfWeek(plan, plan.getCurrentWeeks(), dayOfWeek).isPresent()) {
            throw new AccessDeniedException("이미 계획 완수를 하셨습니다.");
        }

        DailyCheck dailyCheck = DailyCheck.builder()
                .weeks(plan.getCurrentWeeks())
                .dayOfWeek(dayOfWeek)
                // 항상 true 인거 바꾸자!!
                .isCompleted(true)
                .plan(plan)
                .build();

        plan.setActionCount(plan.getActionCount() + 1);
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

        Plan plan = planRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        Feedback feedback = dto.toEntity();
        feedback.setPlan(plan);
        feedback.setUser(user);
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
        selfFeedbackRepository.deleteById(feedbackId);
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
        Feedback entity = dto.toEntity();
        entityManager.merge(entity);
        entityManager.flush();
    }


    /**
     * 셀프 피드백 작성 서비스 메서드
     *
     * @param dto    셀프피드백 작성 dto
     * @param planId 계획 id
     */
    @Transactional
    public void createSelfFeedback(SelfFeedbackDto dto, Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        SelfFeedback selfFeedback = dto.toEntity();
        selfFeedback.setPlan(plan);
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
        SelfFeedback entity = dto.toEntity();
        entityManager.merge(entity);
        entityManager.flush();
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
        Plan plan = planRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획이 DB에 없습니다.");
        });

        User currentUser = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 유저가 없습니다.");
        });

        if (plan.getOwner().equals(currentUser)) {
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
        Plan plan = planRepository.findById(planId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 계획을 찾을 수 없습니다.");
        });

        if (!Objects.equals(plan.getInstructor().getId(), user.getId())) {
            throw new AccessDeniedException("해당 계획의 교관이 아닙니다.");
        }
    }
}
