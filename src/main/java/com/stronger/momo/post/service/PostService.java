package com.stronger.momo.post.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.post.dto.SnsDto;
import com.stronger.momo.post.entity.Post;
import com.stronger.momo.post.repository.CommentRepository;
import com.stronger.momo.post.repository.PostRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<Post> getSnsList(Authentication authentication, Pageable pageable) {
        Long loginUserId = ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
        Page<Post> postList = postRepository.findMyFeed(loginUserId, pageable);
        return postList;
    }

    @Transactional
    public void writePost(Authentication authentication, SnsDto dto) {
        User writer = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        Post post = Post.builder()
                .writer(writer)
                .content(dto.getContent())
                .title(dto.getTitle())
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Authentication authentication, Long snsId) throws AccessDeniedException {
        Post post = postRepository.findById(snsId).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 sns 가 존재하지 않습니다.");
        });

        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        if (!Objects.equals(loginUser.getUsername(), post.getWriter().getUsername())) {
            throw new AccessDeniedException("글 작성자가 아닙니다.");
        }

        postRepository.deleteById(snsId);
    }

    @Transactional
    public void updatePost(Authentication authentication, SnsDto dto) throws AccessDeniedException {
        Post post = postRepository.findById(dto.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("해당 sns 가 존재하지 않습니다.");
        });

        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        if (!Objects.equals(loginUser.getUsername(), post.getWriter().getUsername())) {
            throw new AccessDeniedException("글 작성자가 아닙니다.");
        }

        post.updateSns(dto);
    }

}
