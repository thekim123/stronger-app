package com.stronger.momo.post.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.post.dto.CommentDto;
import com.stronger.momo.post.entity.Comment;
import com.stronger.momo.post.entity.Post;
import com.stronger.momo.post.repository.CommentRepository;
import com.stronger.momo.post.repository.PostRepository;
import com.stronger.momo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void writeComment(Authentication authentication, CommentDto dto) {
        System.out.println(dto);
        User writer = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Post post = postRepository.findById(dto.getPostId()).orElseThrow(() ->
                new EntityNotFoundException("해당 sns 가 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .writer(writer)
                .comment(dto.getComment())
                .post(post)
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

        commentRepository.deleteById(comment.getId());
        commentRepository.flush();
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
