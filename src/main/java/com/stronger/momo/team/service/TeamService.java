package com.stronger.momo.team.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.team.dto.TeamCreateDto;
import com.stronger.momo.team.dto.TeamDto;
import com.stronger.momo.team.dto.TeamMemberDto;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.entity.Grade;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional(readOnly = true)
    public List<TeamMemberDto> retrieveTeamList(Authentication authentication) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        List<TeamMember> teamMemberList =
                teamMemberRepository.findByUser(loginUser);
        System.out.println(teamMemberList);
        return teamMemberList.stream()
                .map(TeamMemberDto::from)
                .collect(Collectors.toList());
    }


    //TODO 그룹장 권한조회 해야함.

    /**
     * @param authentication 로그인 인증 정보
     * @param memberId       그룹 id
     * @return 그룹원 목록 dto
     * @throws AccessDeniedException   권한 없음 예외
     * @throws EntityNotFoundException 그룹 없음 예외
     * @apiNote 그룹원 목록 조회 서비스 로직
     */
    @Transactional(readOnly = true)
    public List<TeamMemberDto> getTeamMemberList(Authentication authentication, Long memberId) throws AccessDeniedException, EntityNotFoundException {
        TeamMember member = teamMemberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);

        List<TeamMember> teamMemberList = teamMemberRepository.findByTeam(member.getTeam());
        return teamMemberList.stream()
                .map(TeamMemberDto::from)
                .collect(Collectors.toList());
    }

    /**
     * TODO 시작날짜와 종료날짜에 관해서 유효성 검사 해야함. 예를 들어 종료날짜가 시작날짜 이전 이라던가, 오늘보다 날짜가 이전이라던가
     *
     * @param authentication 로그인 인증 정보
     * @return 내 그룹 목록 dto
     * @apiNote 내 그룹 목록 조회 서비스 로직
     */
    @Transactional(readOnly = true)
    public List<TeamMemberDto> getMyTeamList(Authentication authentication, String type) {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        List<TeamMemberDto> teamDtoList;
        List<TeamMember> teamMemberList = teamMemberRepository.findByUser(user);
        if (type != null) {
            teamDtoList = teamMemberList.stream()
                    .filter(teamMember -> {
                        if (type.equals("MEMBER")) {
                            return Objects.equals(teamMember.getGrade(), Grade.MEMBER)
                                    || Objects.equals(teamMember.getGrade(), Grade.MANAGER);
                        } else {
                            return Objects.equals(teamMember.getGrade(), Grade.valueOf(type));
                        }
                    })
                    .map(TeamMemberDto::from)
                    .collect(Collectors.toList());
        } else {
            teamDtoList = teamMemberList.stream()
                    .map(TeamMemberDto::from)
                    .collect(Collectors.toList());
        }
        return teamDtoList;
    }

    /**
     * 팀 목록 조회 서비스 로직
     *
     * @return 팀 목록 dto
     * @author thekim123
     * @since version 1.0
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getPublicTeamList(Authentication authentication) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        List<TeamMember> joinedList = teamMemberRepository.findByUser(loginUser);

        LocalDate today = LocalDate.now();
        String jpql = "SELECT t FROM Team t WHERE isOpen=true and t.endDate > :today";
        List<Team> teamList = entityManager.createQuery(jpql, Team.class)
                .setParameter("today", today)
                .getResultList();

        return teamList.stream()
                .filter(team -> joinedList.stream().noneMatch(teamMember -> teamMember.getTeam().equals(team)))
                .map(TeamDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 그룹 생성 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param teamDto        그룹 생성 dto
     */
    @Transactional
    public String createTeam(Authentication authentication, TeamCreateDto teamDto) {
        UUID uuid = UUID.randomUUID();
        String teamCode = uuid.toString();
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        Team team = Team.builder()
                .name(teamDto.getTeamName())
                .description(teamDto.getDescription())
                .owner(owner)
                .isOpen(teamDto.isOpen())
                .startDate(teamDto.getStartDate())
                .endDate(teamDto.getEndDate())
                .teamCode(teamCode)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .grade(Grade.OWNER)
                .team(team)
                .user(owner)
                .build();

        teamRepository.save(team);
        teamMemberRepository.save(teamMember);
        return teamCode;
    }


    /**
     * 그룹 삭제 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param groupId        그룹 id
     * @throws AccessDeniedException 그룹장이 아닌 경우 거절
     */
    @Transactional
    public void deleteGroup(Authentication authentication, Long groupId) throws AccessDeniedException {
        Team team = teamRepository.findById(groupId).orElseThrow(() -> {
            throw new EntityNotFoundException("팀이 존재하지 않습니다.");
        });

        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        User owner = team.getOwner();
        if (!Objects.equals(owner.getUsername(), loginUser.getUsername())) {
            throw new AccessDeniedException("팀의 소유자가 아닙니다.");
        }

        teamRepository.deleteById(team.getId());
    }


    /**
     * 그룹 수정 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param dto            그룹 dto
     * @throws AccessDeniedException 그룹장이 아닌 경우 거절
     */
    @Transactional
    public void updateTeam(Authentication authentication, TeamDto dto) throws AccessDeniedException {
        Team team = teamRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("팀이 존재하지 않습니다.");
        });

        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        User owner = team.getOwner();
        if (!Objects.equals(owner.getUsername(), loginUser.getUsername())) {
            throw new AccessDeniedException("팀의 소유자가 아닙니다.");
        }

        team.update(dto);
    }


    /**
     * 그룹 가입 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param groupId        그룹 id
     * @throws AccessDeniedException 그룹장이 아닌 경우 거절
     */
    @Transactional
    public void joinTeam(Authentication authentication, Long groupId) throws AccessDeniedException {
        Team team = teamRepository.findById(groupId).orElseThrow(() -> {
            throw new EntityNotFoundException("팀이 존재하지 않습니다");
        });

        User joinUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        TeamMember teamMember = TeamMember.builder()
                .grade(Grade.PENDING)
                .team(team)
                .user(joinUser)
                .build();
        teamMemberRepository.save(teamMember);
    }


    @Transactional
    public void privateJoinTeam(Authentication authentication, String teamCode) {
        User user = ((PrincipalDetails) authentication.getPrincipal())
                .getUser();
        Team team = teamRepository
                .findByTeamCode(teamCode)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("팀이 존재하지 않습니다.");
                });

        TeamMember teamMember =
                TeamMember.builder()
                        .user(user)
                        .team(team)
                        .grade(Grade.PENDING)
                        .build();
        teamMemberRepository.save(teamMember);
    }

    /**
     * TODO : JPQL로 변경해야함 조회만 3번함
     *
     * @param authentication 로그인 인증 정보
     * @param dto            직책 변경 dto
     * @throws AccessDeniedException 그룹장이 아닌 경우 거절
     * @apiNote 팀 직책 변경 서비스 로직
     */
    @Transactional
    public void updatePosition(
            Authentication authentication,
            TeamMemberDto dto) throws AccessDeniedException {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀이 존재하지 않습니다");
        });

        TeamMember teamMember =
                teamMemberRepository.findById(dto.getId()).orElseThrow(() -> {
                    throw new EntityNotFoundException("해당 팀원이 존재하지 않습니다.");
                });

        if (!Objects.equals(team.getOwner().getUsername(), loginUser.getUsername())) {
            throw new AccessDeniedException("팀장이 아닙니다. 권한이 없습니다.");
        }

        teamMember.update(dto.getGradeName());
    }

    /**
     * 팀 탈퇴 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param memberId       팀원 id
     * @throws AccessDeniedException 신청 정보가 본인이 아닌 경우
     */
    @Transactional
    public void leaveTeam(Authentication authentication, Long memberId) throws AccessDeniedException {
        TeamMember teamMember = teamMemberRepository.findById(memberId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원은 존재하지 않습니다.");
        });

        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        if (!user.getUsername().equals(teamMember.getUser().getUsername())) {
            throw new AccessDeniedException("탈퇴는 본인만 할 수 있습니다.");
        }

        teamMemberRepository.deleteById(teamMember.getId());
    }

    /**
     * 팀 맴버 추방 서비스 로직
     *
     * @param authentication 로그인 유저 정보
     * @param dto            직책 dto
     * @throws AccessDeniedException 그룹장이 아닌 경우
     */
    @Transactional
    public void banUser(Authentication authentication, TeamMemberDto dto) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀이 존재하지 않습니다.");
        });

        if (!owner.getUsername().equals(team.getOwner().getUsername())) {
            throw new AccessDeniedException("팀장만이 그룹원을 추방시킬 수 있습니다.");
        }

        TeamMember teamMember = teamMemberRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원은 존재하지 않습니다.");
        });
        teamMemberRepository.deleteById(teamMember.getId());
    }

}