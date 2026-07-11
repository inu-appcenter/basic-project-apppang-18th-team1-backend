package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuickReply {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String buttonText;
    private String triggerCondition;

    @Builder
    public QuickReply(String buttonText, String triggerCondition){
        this.buttonText =buttonText;
        this.triggerCondition =triggerCondition;

    }
}
