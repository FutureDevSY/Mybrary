package com.mybrary.backend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecessionRequestDto {

    /**
     *  탈퇴 요청
     */

    private String email;
    private String password;
    private String passwordConfirm;
    private boolean confirmation;
    private String feedback;

}
