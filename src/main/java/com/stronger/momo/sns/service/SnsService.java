package com.stronger.momo.sns.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.sns.dto.CommentDto;
import com.stronger.momo.sns.dto.SnsDto;
import com.stronger.momo.sns.entity.Comment;
import com.stronger.momo.sns.entity.Sns;
import com.stronger.momo.sns.repository.CommentRepository;
import com.stronger.momo.sns.repository.SnsRepository;
import com.stronger.momo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SnsService {

    private final SnsRepository snsRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<Sns> getSnsList(Authentication authentication, Pageable pageable) {
        Long loginUserId = ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
        Page<Sns> snsList = snsRepository.findMyFeed(loginUserId, pageable);
        return snsList;
    }

    @Transactional
    public void writeSns(Authentication authentication, SnsDto dto) {
        User writer = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        Sns sns = Sns.builder()
                .writer(writer)
                .content(dto.getContent())
                .title(dto.getTitle())
                .build();

        snsRepository.save(sns);
    }

    @Transactional
    public void deleteSns(Authentication authentication, Long snsId) throws AccessDeniedException {
        Sns sns = snsRepository.findById(snsId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 sns 가 존재하지 않습니다.");
        });

        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        if (!Objects.equals(loginUser.getUsername(), sns.getWriter().getUsername())) {
            throw new AccessDeniedException("글 작성자가 아닙니다.");
        }

        snsRepository.deleteById(snsId);
    }

    @Transactional
    public void updateSns(Authentication authentication, SnsDto dto) throws AccessDeniedException {
        Sns sns = snsRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 sns 가 존재하지 않습니다.");
        });

        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        if (!Objects.equals(loginUser.getUsername(), sns.getWriter().getUsername())) {
            throw new AccessDeniedException("글 작성자가 아닙니다.");
        }

        sns.updateSns(dto);
    }

    @Transactional
    public void writeComment(Authentication authentication, CommentDto dto) {
        User writer = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Sns sns = snsRepository.findById(dto.getSnsId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 sns 가 존재하지 않습니다.");
        });

        Comment comment = Comment.builder()
                .writer(writer)
                .comment(dto.getComment())
                .sns(sns)
                .build();
        commentRepository.save(comment);
    }


    @Transactional
    public void deleteComment(Authentication authentication, Long commentId) throws AccessDeniedException {
        User writer = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 댓글이 존재하지 않습니다.");
        });

        if (!Objects.equals(writer.getUsername(), comment.getWriter().getUsername())) {
            throw new AccessDeniedException("댓글 작성자만이 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }


    @Transactional
    public void updateComment(Authentication authentication, CommentDto dto) {
        User writer = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Comment comment = commentRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 댓글이 존재하지 않습니다.");
        });

        if (!Objects.equals(writer.getUsername(), comment.getWriter().getUsername())) {
            throw new AccessDeniedException("댓글 작성자만이 삭제할 수 있습니다.");
        }

        comment.updateComment(dto);
        commentRepository.save(comment);
    }

}
