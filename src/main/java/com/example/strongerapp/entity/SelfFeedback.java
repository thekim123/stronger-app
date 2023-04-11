package com.example.strongerapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 자가피드백 Entity
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SelfFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Plan plan;

    // 실패 이유
    private String reason;

    // 대책
    private String measure;


}
