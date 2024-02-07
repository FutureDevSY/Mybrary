package com.mybrary.backend.domain.contents.paper.dto;

import com.mybrary.backend.domain.image.dto.ImagePostDto;
import java.util.List;
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
public class PaperUpdateDto {

    /**
     *  스레드 수정 요청
     *  스레드 저장과 마찬가지로 페이퍼리스트 포함
     */

    private int layoutType;
    private String content1;
    private String content2;
    private Long bookId;
    private boolean isPaperPublic;
    private boolean isScrapEnable;
    private Long imageId1;
    private Long imageId2;
    private List<String> tagList;

}
