package com.stronger.momo.post.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.post.entity.Subscribe;
import com.stronger.momo.post.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;

    @Transactional
    public void subscribe(Authentication authentication, Long toUserId) {
        Long loginUserId = ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
        subscribeRepository.subscribe(loginUserId, toUserId);
    }

    @Transactional
    public void unSubscribe(Authentication authentication, Long fromUserId) {
        Long loginUserId = ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
        subscribeRepository.unSubscribe(loginUserId, fromUserId);
    }

}
