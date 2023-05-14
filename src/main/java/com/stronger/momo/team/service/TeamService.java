package com.stronger.momo.team.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.team.dto.TeamDto;
import com.stronger.momo.team.dto.TeamMemberDto;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.team.entity.Grade;
import com.stronger.momo.team.repository.TeamMemberRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.team.repository.TeamRepository;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    /**
     * 그룹원 목록 조회 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param teamId         그룹 id
     * @return 그룹원 목록 dto
     * @throws AccessDeniedException   권한 없음 예외
     * @throws EntityNotFoundException 그룹 없음 예외
     */
    @Transactional(readOnly = true)
    public List<TeamMemberDto> getTeamMemberList(Authentication authentication, Long teamId) throws AccessDeniedException, EntityNotFoundException {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(teamId).orElseThrow(EntityNotFoundException::new);

        if (teamMemberRepository.findByUserAndTeam(user, team).isEmpty()) {
            throw new AccessDeniedException("해당 그룹에 가입되어 있지 않습니다.");
        }

        List<TeamMember> teamMemberList = teamMemberRepository.findByTeam(team);
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
    public List<TeamMemberDto> getMyTeamList(Authentication authentication) {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        List<TeamMemberDto> teamDtoList = new ArrayList<>();

        List<TeamMember> teamMemberList = teamMemberRepository.findByUser(user);
        teamMemberList.stream()
                .map(TeamMemberDto::from)
                .forEach(teamDtoList::add);
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
    public List<TeamDto> getPublicTeamList() {
        List<Team> teamList = teamRepository.findAllByIsOpenTrue();
        return teamList.stream()
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
    public void createTeam(Authentication authentication, TeamDto teamDto) {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = Team.builder()
                .name(teamDto.getTeamName())
                .description(teamDto.getDescription())
                .owner(owner)
                .isOpen(teamDto.isOpen())
                .startDate(teamDto.getStartDate())
                .endDate(teamDto.getEndDate())
                .build();

        TeamMember teamMember = TeamMember.builder()
                .grade(Grade.OWNER)
                .team(team)
                .user(owner)
                .build();

        teamRepository.save(team);
        teamMemberRepository.save(teamMember);
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
    public void joinGroup(Authentication authentication, Long groupId) throws AccessDeniedException {
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


    /**
     * TODO : JPQL로 변경해야함 조회만 3번함
     *
     * @param authentication 로그인 인증 정보
     * @param dto            직책 변경 dto
     * @throws AccessDeniedException 그룹장이 아닌 경우 거절
     * @apiNote 팀 직책 변경 서비스 로직
     */
    @Transactional
    public void updatePosition(Authentication authentication, TeamMemberDto dto) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀이 존재하지 않습니다");
        });

        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 유저가 존재하지 않습니다.");
        });

        TeamMember teamMember = teamMemberRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 존재하지 않습니다.");
        });

        if (!Objects.equals(team.getOwner().getUsername(), owner.getUsername())) {
            throw new AccessDeniedException("팀장이 아닙니다. 권한이 없습니다.");
        }

        teamMember.update(user, team, dto.getGradeName());
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