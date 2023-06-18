package com.stronger.momo.post.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.stronger.momo.common.aws.AwsS3;
import com.stronger.momo.common.aws.AwsS3Service;
import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.goal.repository.PlanRepository;
import com.stronger.momo.post.dto.PostCreateDto;
import com.stronger.momo.post.entity.Post;
import com.stronger.momo.post.repository.PostRepository;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;

    @Transactional(readOnly = true)
    public Page<Post> getSnsList(Authentication authentication, Pageable pageable) {
        Long loginUserId = ((PrincipalDetails) authentication.getPrincipal()).getUser().getId();
        Page<Post> postList = postRepository.findMyFeed(loginUserId, pageable);
        System.out.println(postList);

        postList.forEach(post -> {
            post.setLikeCount(post.getLikes().size());
            post.getLikes().forEach(like -> {
                if (Objects.equals(like.getUser().getId(), loginUserId)) {
                    post.setLikeState(true);
                }
            });
        });

        return postList;
    }

    @Transactional
    public void writePost(Authentication authentication, PostCreateDto dto) throws IOException {
        User writer = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Plan plan = planRepository.findById(dto.getPlanId()).orElseThrow(() ->
                new EntityNotFoundException("해당 plan 이 존재하지 않습니다."));
        AwsS3 uploadResult = awsS3Service.upload(dto.getPostImage(), "post");

        Post post = Post.builder()
                .writer(writer)
                .content(dto.getContent())
                .title(dto.getTitle())
                .team(plan.getMember().getTeam())
                .postImageKey(uploadResult.getKey())
                .postImageUrl(uploadResult.getPath())
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Authentication authentication, Long postId) throws AccessDeniedException {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("해당 sns 가 존재하지 않습니다."));
        System.out.println("post");
        String loginUsername = ((PrincipalDetails) authentication.getPrincipal()).getUser().getUsername();
        System.out.println("loginUser");
        if (!Objects.equals(loginUsername, post.getWriter().getUsername())) {
            throw new AccessDeniedException("글 작성자가 아닙니다.");
        }

        postRepository.deleteById(postId);
    }

    @Transactional
    public void updatePost(Authentication authentication, PostCreateDto dto) throws AccessDeniedException {
        Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("해당 sns 가 존재하지 않습니다."));

        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        if (!Objects.equals(loginUser.getUsername(), post.getWriter().getUsername())) {
            throw new AccessDeniedException("글 작성자가 아닙니다.");
        }

        post.updateSns(dto);
    }

    @Transactional(readOnly = true)
    public Page<Post> getProfileSnsList(Authentication authentication, Long userId, Pageable pageable) {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        Page<Post> posts;
        if (Objects.equals(loginUser.getId(), user.getId())) {
            posts = postRepository.findByWriter(user, pageable);
            return posts;
        }
        posts = postRepository.findOthersPost(user, pageable);
        System.out.println("others: "+posts);
        return posts;
    }
}
