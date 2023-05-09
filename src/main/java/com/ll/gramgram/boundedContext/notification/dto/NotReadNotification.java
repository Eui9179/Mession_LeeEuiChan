package com.ll.gramgram.boundedContext.notification.dto;

import com.ll.gramgram.boundedContext.notification.entity.TypeCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class NotReadNotification {
    private String dateTime;
    private String fromInstaMemberUsername;
    private String gender;
    private TypeCode typeCode;
    private String oldAttractiveType;
    private String newAttractiveType;
}
