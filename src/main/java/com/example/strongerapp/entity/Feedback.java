package com.example.strongerapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 선생님 피드백 Entity
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Feedback feedback;

    private String comment;

}
