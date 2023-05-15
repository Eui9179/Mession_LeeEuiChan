package com.ll.gramgram.boundedContext.likeablePerson.dto.request;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToListSearchForm {
    private String gender;
    private Integer attractiveTypeCode;
    private int sortCode = 1; // 기본값
}