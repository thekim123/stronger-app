package com.stronger.momo.team.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.team.dto.TeamDto;
import com.stronger.momo.team.dto.GradeDto;
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

    @Transactional(readOnly = true)
    public List<TeamDto> getMyTeamList(Authentication authentication) {
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        List<Team> teamList = teamRepository.findByOwner(user);
        return teamList.stream()
                .map(TeamDto::from)
                .collect(Collectors.toList());
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
    public void createGroup(Authentication authentication, TeamDto teamDto) {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = Team.builder()
                .groupName(teamDto.getTeamName())
                .description(teamDto.getDescription())
                .owner(owner)
                .isOpen(teamDto.isOpen())
                .build();

        TeamMember teamMember = TeamMember.builder()
                .grade(Grade.OWNER)
                .team(team)
                .member(owner)
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
    public void updateGroup(Authentication authentication, TeamDto dto) throws AccessDeniedException {
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
                .member(joinUser)
                .build();
        teamMemberRepository.save(teamMember);
    }


    /**
     * 팀 직책 변경 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param dto            직책 변경 dto
     * @throws AccessDeniedException 그룹장이 아닌 경우 거절
     */
    @Transactional
    public void updatePosition(Authentication authentication, GradeDto dto) throws AccessDeniedException {
        User owner = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Team team = teamRepository.findById(dto.getTeamId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀이 존재하지 않습니다");
        });

        User user = userRepository.findById(dto.getMemberId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 유저가 존재하지 않습니다.");
        });

        TeamMember teamMember = teamMemberRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원이 존재하지 않습니다.");
        });

        if (!Objects.equals(team.getOwner().getUsername(), owner.getUsername())) {
            throw new AccessDeniedException("팀장이 아닙니다. 권한이 없습니다.");
        }

        teamMember.update(user, team, dto.getPositionName());
    }

    /**
     * 팀 탈퇴 서비스 로직
     *
     * @param authentication 로그인 인증 정보
     * @param dto            직책 dto
     * @throws AccessDeniedException 신청 정보가 본인이 아닌 경우
     */
    @Transactional
    public void leaveTeam(Authentication authentication, GradeDto dto) throws AccessDeniedException {
        TeamMember teamMember = teamMemberRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 팀원은 존재하지 않습니다.");
        });

        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        if (!user.getUsername().equals(teamMember.getMember().getUsername())) {
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
    public void banUser(Authentication authentication, GradeDto dto) throws AccessDeniedException {
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