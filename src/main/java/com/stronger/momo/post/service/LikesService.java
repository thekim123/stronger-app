package com.stronger.momo.post.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.post.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;

    @Transactional
    public void likePost(Authentication authentication, Long postId) {
        Long loginUserId = ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
        likesRepository.mLikes(postId, loginUserId);
    }

    @Transactional
    public void unLikePost(Authentication authentication, Long postId) {
        Long loginUserId = ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
        likesRepository.mUnLikes(postId, loginUserId);
    }

}
