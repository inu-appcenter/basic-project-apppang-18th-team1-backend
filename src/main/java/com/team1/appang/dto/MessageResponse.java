package com.team1.appang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

//messageResponse만 보낼때 사용하는 DTO
//객체를 생성과 함께 값을 넣을것이므로 Setter는 필요하지 않음
@Getter
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
