package com.example.strongerapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * SNS Entity
 * 계획 실천 중 공유할 때 사용
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Sns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

}
