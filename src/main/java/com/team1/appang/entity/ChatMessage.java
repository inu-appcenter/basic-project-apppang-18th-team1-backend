package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String senderType; //타입, 봇/유저

    @Lob //TEXT 타입과 매핑하기 위함
    @Column(nullable = false)
    private String messageText;


    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Builder
    public ChatMessage(String senderType, String messageText, ChatRoom chatRoom){
        this.senderType = senderType;
        this.messageText = messageText;
        this.chatRoom =chatRoom;
    }

}
