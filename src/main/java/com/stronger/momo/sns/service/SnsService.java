package com.stronger.momo.sns.service;

import com.stronger.momo.sns.repository.SnsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnsService {

    private final SnsRepository snsRepository;

}
