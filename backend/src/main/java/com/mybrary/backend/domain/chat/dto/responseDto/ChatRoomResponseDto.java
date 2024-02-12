package com.mybrary.backend.domain.chat.dto.responseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponseDto {

    private Long chatRoomId;
    private Long otherMemberId;
    private String otherMemberEmail;
    private String otherMemberNickname;
    private String otherMemberProfileImageUrl;
    private String latestMessage;


    public ChatRoomResponseDto(Long chatRoomId, Long otherMemberId, String otherMemberEmail,
                               String otherMemberNickname, String otherMemberProfileImageUrl, String latestMessage) {
        this.chatRoomId = chatRoomId;
        this.otherMemberId = otherMemberId;
        this.otherMemberEmail = otherMemberEmail;
        this.otherMemberNickname = otherMemberNickname;
        this.otherMemberProfileImageUrl = otherMemberProfileImageUrl;
        this.latestMessage = latestMessage;
    }

    @Setter
    private Long unreadMessageCount;

}
