package com.stronger.momo.team.controller;

import com.stronger.momo.team.dto.TeamDto;
import com.stronger.momo.team.dto.TeamMemberDto;
import com.stronger.momo.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * 그룹과 관련된 API
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/team")
public class TeamController {

    private final TeamService teamService;

    /**
     * @apiNote 그룹원 목록 조회 API
     * @param authentication 로그인 인증 정보
     * @param memberId       그룹장 id
     * @return 그룹 생성 dto
     */
    @GetMapping("/owner/{memberId}")
    public ResponseEntity<?> getTeamMemberList(Authentication authentication, @PathVariable Long memberId) {
        List<TeamMemberDto> dtoList = teamService.getTeamMemberList(authentication, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(dtoList);
    }

    /**
     * 내 그룹 목록 조회 API
     *
     * @param authentication 로그인 인증 정보
     * @return 내 그룹 목록 dto
     */
    @GetMapping("/my-list")
    public ResponseEntity<?> getMyTeamList(Authentication authentication) {
        List<TeamMemberDto> myList = teamService.getMyTeamList(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(myList);
    }

    /**
     * 팀 목록 조회 API
     *
     * @return 팀 목록 dto
     */
    @GetMapping("/list")
    public ResponseEntity<?> getPublicTeamList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(teamService.getPublicTeamList());
    }

    /**
     * 그룹 생성 API
     *
     * @param authentication 로그인 인증 정보
     * @param teamDto        팀 생성 dto
     */
    @PostMapping("/create")
    public ResponseEntity<?> createGroup(Authentication authentication, @RequestBody TeamDto teamDto) {
        teamService.createTeam(authentication, teamDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("팀 생성이 완료되었습니다.");
    }


    /**
     * 그룹 삭제 API
     *
     * @param authentication 로그인 인증 정보
     * @param teamId         그룹 id
     * @throws AccessDeniedException 그룹장이 아닌 경우 거절
     */
    @DeleteMapping("/delete/{teamId}")
    public ResponseEntity<?> deleteGroup(Authentication authentication, @PathVariable Long teamId) throws AccessDeniedException {
        teamService.deleteGroup(authentication, teamId);
        return ResponseEntity.status(HttpStatus.OK).body("팀 삭제가 완료되었습니다.");
    }


    /**
     * 그룹 수정 API
     *
     * @param authentication 로그인 인증 정보
     * @param dto            그룹 dto
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateTeam(Authentication authentication, @RequestBody TeamDto dto) {
        System.out.println(dto);
        teamService.updateTeam(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("팀 정보 수정이 완료되었습니다.");
    }

    /**
     * 그룹 가입 API
     *
     * @param authentication 로그인 인증 정보
     * @param groupId        그룹 id
     */
    @PostMapping("/join/{groupId}")
    public ResponseEntity<?> joinGroup(Authentication authentication
            , @PathVariable Long groupId
            , String introduce) {
        System.out.println(introduce);
        teamService.joinGroup(authentication, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body("팀 가입 신청이 완료되었습니다.");
    }

    /**
     * 그룹 직책 변경 API
     *
     * @param authentication 로그인 인증 정보
     * @param dto            직책 변경 dto
     */
    @PutMapping("/position")
    public ResponseEntity<?> updatePosition(Authentication authentication, @RequestBody TeamMemberDto dto) {
        teamService.updatePosition(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("팀 멤버의 직책을 바꿨습니다.");
    }

    @DeleteMapping("/leave/{memberId}")
    public ResponseEntity<?> leaveGroup(Authentication authentication, @PathVariable Long memberId) {
        teamService.leaveTeam(authentication, memberId);
        return ResponseEntity.status(HttpStatus.OK).body("팀 탈퇴가 완료되었습니다.");
    }

    @DeleteMapping("/ban")
    public ResponseEntity<?> banUser(Authentication authentication, @RequestBody TeamMemberDto dto) throws AccessDeniedException {
        teamService.banUser(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("팀 회원 추방이 완료되었습니다.");
    }

}
